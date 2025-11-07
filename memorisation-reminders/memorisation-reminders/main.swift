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
    guard let basePath: String = ProcessInfo.processInfo.environment["REPO_PATH"] else {
        print("No REPO_PATH env. variable")
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

    let baseDueComps = nextSevenAM()

    for (index, day) in schedule.days.enumerated() {
        let reminder = EKReminder(eventStore: eventStore)
        reminder.title = day.passages
        reminder.notes = "\(day.verses) verses"

        // Assign to the Memorisation list
        reminder.calendar = reminderList

        // Due date: base 7:00 AM plus index days so each reminder starts on a different day in the cycle
        var dueComps = baseDueComps
        // Add index days to the base due date components
        if let baseDate = calendar.date(from: baseDueComps),
           let offsetDate = calendar.date(byAdding: .day, value: index, to: baseDate) {
            dueComps = calendar.dateComponents([.year, .month, .day, .hour, .minute, .second], from: offsetDate)
        }
        reminder.dueDateComponents = dueComps

        // Recurrence: repeat every totalDays days
        if totalDays > 1 {
            let rule = EKRecurrenceRule(recurrenceWith: .daily, interval: totalDays, end: nil)
            reminder.recurrenceRules = [rule]
        } else {
            reminder.recurrenceRules = nil
        }

        do {
            try eventStore.save(reminder, commit: false)
            print("Scheduled reminder: \(reminder.title ?? "(no title)") at 7:00 AM, repeating every \(max(totalDays,1)) day(s)")
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
