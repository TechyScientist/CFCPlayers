//
//  PlayerResponse.swift
//  CFCPlayers
//
//  Created by Johnny Console on 2023-07-30.
//

import Foundation

class PlayerResponse: Decodable {
    
    var updated: String
    var player: Player
    
    private enum CodingKeys: String, CodingKey {
        case updated
        case player
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        updated = try container.decode(String.self, forKey: .updated)
        player = try container.decode(Player.self, forKey: .player)
        player.setUpdated(updated)
    }
}
