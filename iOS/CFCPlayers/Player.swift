//
//  Player.swift
//  CFCPlayers
//
//  Created by Johnny Console on 2023-07-30.
//

import Foundation

class Player: Decodable {
    
    var cfcID: Int
    var expiry: String
    var fideID: Int64
    var name: String
    var cityProv: String
    var regular: Int
    var quick: Int
    var updated: String?
    
    private enum CodingKeys: String, CodingKey {
        case cfc_id
        case cfc_expiry
        case fide_id
        case name_first
        case name_last
        case addr_city
        case addr_province
        case regular_rating
        case quick_rating
    }
    
    required init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        cfcID = try container.decode(Int.self, forKey: .cfc_id)
        expiry = try container.decode(String.self, forKey: .cfc_expiry)
        fideID = try container.decode(Int64.self, forKey: .fide_id)
        let firstName = try container.decode(String.self, forKey: .name_first)
        let lastName = try container.decode(String.self, forKey:.name_last)
        
        name = (lastName.isEmpty || lastName == ".") && (firstName.isEmpty || firstName == ".") ? "Unidentified Player" : lastName.isEmpty || lastName == "." ? "\(firstName)" : firstName.isEmpty || firstName == "." ? "\(lastName)" : "\(lastName), \(firstName)"
        let city = try container.decode(String.self, forKey: .addr_city)
        let prov = try container.decode(String.self, forKey: .addr_province)
        cityProv = city.isEmpty && prov.isEmpty ? "Unknown" : city.isEmpty ? "\(prov)" : prov.isEmpty ? "\(city)" : "\(city), \(prov)"
        regular = try container.decode(Int.self, forKey: .regular_rating)
        quick = try container.decode(Int.self, forKey: .quick_rating)
    }
    
    func setUpdated(_ updated: String) {
        self.updated = updated
    }
}
