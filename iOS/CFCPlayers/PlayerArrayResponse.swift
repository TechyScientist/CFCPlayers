//
//  PlayerArrayResponse.swift
//  CFCPlayers
//
//  Created by Johnny Console on 2023-07-30.
//

import Foundation

class PlayerArrayResponse: Decodable {
    
    var updated: String
    var players: [Player]
    
    private enum CodingKeys: String, CodingKey {
        case updated
        case players
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        updated = try container.decode(String.self, forKey: .updated)
        players = try container.decode([Player].self, forKey: .players)
        players.forEach { player in
            player.setUpdated(updated)
        }
    }
}
