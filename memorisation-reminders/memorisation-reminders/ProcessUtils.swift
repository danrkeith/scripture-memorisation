//
//  ProcessUtils.swift
//  memorisation-reminders
//
//  Created by Daniel Keith on 7/11/2025.
//

import Foundation

let javaPath = "/usr/bin/java"

func runJar(path: String, args: String...) throws {
    let process = Process()
    process.executableURL = URL(fileURLWithPath: javaPath)
    process.arguments = ["-jar", path] + args
    
    try process.run()
    process.waitUntilExit()
}

func readJson<T: Decodable>(_ file: String) -> T? {
    let fileURL = URL(fileURLWithPath: file)
    guard let data = try? Data(contentsOf: fileURL) else {
        return nil
    }
    
    let decoder = JSONDecoder()
    return try? decoder.decode(T.self, from: data)
}
