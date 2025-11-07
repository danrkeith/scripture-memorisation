//
//  main.swift
//  memorisation-reminders
//
//  Created by Daniel Keith on 6/11/2025.
//

import Foundation
import EventKit

struct Day: Codable {
    let passages: String
    let verses: Int
}

struct Schedule: Codable {
    let days: [Day]
}

func main() {
    // Run scheduling
    guard let basePath: String = ProcessInfo.processInfo.environment["SM_REPO_PATH"] else {
        print("No SM_REPO_PATH env. variable")
        exit(1)
    }
    
    let jarPath: String = "\(basePath)/memorisation-scheduling/out/artifacts/memorisation_scheduling_jar/memorisation-scheduling.jar"
    let inputPath: String = "\(basePath)/input.json"
    let outputPath: String = "\(basePath)/output.json"

    // Run scheduling
    do {
        try runJar(path: jarPath, args: inputPath, outputPath)
    } catch {
        print("Error running jar")
        exit(1)
    }

    // Read output of scheduling
    guard let schedule: Schedule = readJson(outputPath) else {
        print("Error reading JSON")
        exit(1)
    }
    
    // Authorise reminders
    guard let eventStore: EKEventStore = eventStoreWithAccessToReminders() else {
        print("Failed to grant access to reminders")
        exit(1)
    }
    
    // Find or create the "Memorisation" reminders list
    let reminderListName = "Memorisation"
    let reminderList: EKCalendar

    if let existing = getReminderList(eventStore: eventStore, name: reminderListName) {
        reminderList = existing
    } else {
        do {
            reminderList = try createReminderList(eventStore: eventStore, name: reminderListName)
            print("Created reminders list: \(reminderListName)")
        } catch {
            print("Failed to create reminders list '\(reminderListName)': \(error.localizedDescription)")
            exit(1)
        }
    }

    // Clear all existing reminders in the target list
    clearReminderList(eventStore: eventStore, reminderList: reminderList)
    
    let calendar = Calendar.current
    let totalDays = schedule.days.count

    // Helper: compute the next 7:00 AM from now
    func nextSevenAM(from now: Date = Date()) -> DateComponents {
        var comps = calendar.dateComponents([.year, .month, .day], from: now)
        comps.hour = 7
        comps.minute = 0
        comps.second = 0
        // If it's already past 7:00 AM today, move to tomorrow 7:00 AM
        if let todaySeven = calendar.date(from: comps), todaySeven <= now {
            if let tomorrow = calendar.date(byAdding: .day, value: 1, to: todaySeven) {
                let t = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: tomorrow)
                return t
            }
        }
        return calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: calendar.date(from: comps) ?? now)
    }

    // Determine base due date components based on command line argument (YYYY-MM-DD) or default to next 7:00 AM
    let args = CommandLine.arguments.dropFirst() // drop executable path
    let baseDueComps: DateComponents
    if let dateArg = args.first, args.count == 1 {
        // Parse YYYY-MM-DD
        let parts = dateArg.split(separator: "-")
        if parts.count == 3,
           let year = Int(parts[0]),
           let month = Int(parts[1]),
           let day = Int(parts[2]) {
            var comps = DateComponents()
            comps.year = year
            comps.month = month
            comps.day = day
            comps.hour = 7
            comps.minute = 0
            comps.second = 0
            baseDueComps = comps
        } else {
            print("Invalid date argument. Expected format YYYY-MM-DD. Falling back to next 7:00 AM.")
            baseDueComps = nextSevenAM()
        }
    } else if args.isEmpty {
        baseDueComps = nextSevenAM()
    } else {
        print("Unexpected arguments: \(args). Expected zero args or a single YYYY-MM-DD. Falling back to next 7:00 AM.")
        baseDueComps = nextSevenAM()
    }

    for (index, day) in schedule.days.enumerated() {
        let reminder = EKReminder(eventStore: eventStore)
        reminder.title = day.passages
        reminder.notes = "\(day.verses) verses"
        reminder.calendar = reminderList

        // Base 7:00 AM due date plus index days
        guard let baseDate = calendar.date(from: baseDueComps) else { continue }
        var dueDate = calendar.date(byAdding: .day, value: index, to: baseDate)!

        // Skip past dates — move forward by full recurrence cycles until dueDate >= now
        let now = Date()
        if dueDate < now && totalDays > 0 {
            // Compute how many full cycles we need to skip ahead
            let daysToAdd = ((calendar.dateComponents([.day], from: dueDate, to: now).day ?? 0) / totalDays + 1) * totalDays
            dueDate = calendar.date(byAdding: .day, value: daysToAdd, to: dueDate)!
        }

        // Convert to components
        let dueComps = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: dueDate)
        reminder.dueDateComponents = dueComps

        // Recurrence rule: repeat every totalDays days
        if totalDays > 1 {
            let rule = EKRecurrenceRule(recurrenceWith: .daily, interval: totalDays, end: nil)
            reminder.recurrenceRules = [rule]
        }

        do {
            try eventStore.save(reminder, commit: false)
            print("Scheduled reminder: \(reminder.title ?? "(no title)") for \(dueDate), repeating every \(max(totalDays, 1)) day(s)")
        } catch {
            print("Failed to save reminder for \(day.passages): \(error.localizedDescription)")
        }
    }


    // Commit all changes at once
    do {
        try eventStore.commit()
    } catch {
        print("Failed to commit reminders: \(error.localizedDescription)")
        exit(1)
    }
    
    
}

main()
