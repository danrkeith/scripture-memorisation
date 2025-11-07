//
//  ReminderUtils.swift
//  memorisation-reminders
//
//  Created by Daniel Keith on 7/11/2025.
//

import Foundation
import EventKit

func eventStoreWithAccessToReminders() -> EKEventStore? {
    let eventStore = EKEventStore()
    let semaphore = DispatchSemaphore(value: 0)
    var accessGranted = false

    eventStore.requestFullAccessToReminders { granted, error in
        if let error = error {
            print("Reminders access request failed: \(error.localizedDescription)")
        }
        accessGranted = granted
        semaphore.signal()
    }

    // Wait for the authorization result before continuing
    _ = semaphore.wait(timeout: .distantFuture)

    guard accessGranted else {
        print("Error granting access to reminders")
        return nil
    }
    
    return eventStore
}

func createReminderList(eventStore: EKEventStore, name: String) throws -> EKCalendar {
    // Create a new calendar for reminders
    let calendar = EKCalendar(for: .reminder, eventStore: eventStore)
    calendar.title = name
    
    // Use the default source for new reminders (usually iCloud)
    let reminderCalendars = eventStore.calendars(for: .reminder)
    
    if let defaultSource = reminderCalendars.first?.source ?? eventStore.defaultCalendarForNewReminders()?.source {
        calendar.source = defaultSource
    } else if let localSource = eventStore.sources.first(where: { $0.sourceType == .local }) {
        calendar.source = localSource
    }
    
    try eventStore.saveCalendar(calendar, commit: true)
    
    return calendar
}

func getReminderList(eventStore: EKEventStore, name: String) -> EKCalendar? {
    let reminderCalendars = eventStore.calendars(for: .reminder)

    return reminderCalendars.first(where: { $0.title == name })
}

func clearReminderList(eventStore: EKEventStore, reminderList: EKCalendar) {
    let predicate = eventStore.predicateForReminders(in: [reminderList])
    let group = DispatchGroup()
    group.enter()
    eventStore.fetchReminders(matching: predicate) { reminders in
        if let reminders = reminders {
            for r in reminders {
                do {
                    try eventStore.remove(r, commit: false)
                } catch {
                    print("Failed to remove reminder '\(r.title ?? "")': \(error.localizedDescription)")
                }
            }
        }
        group.leave()
    }
    group.wait()
    do {
        try eventStore.commit()
        print("Cleared existing reminders in '\(reminderList.title)'.")
    } catch {
        print("Failed to commit deletions: \(error.localizedDescription)")
        exit(1)
    }
}
