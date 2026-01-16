//
//  PlayerDetailsViewController.swift
//  CFCPlayers
//
//  Created by Johnny Console on 2023-07-31.
//

import UIKit

class PlayerDetailsViewController: UIViewController {
    
    @IBOutlet weak var name: UILabel!
    @IBOutlet weak var cfcID: UIButton!
    @IBOutlet weak var cfcExpiry: UILabel!
    @IBOutlet weak var fideID: UIButton!
    @IBOutlet weak var cityProv: UILabel!
    @IBOutlet weak var regular: UILabel!
    @IBOutlet weak var quick: UILabel!
    @IBOutlet weak var lastUpdated: UILabel!
    
    var profile: Player?

    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        if let player = profile {
            navigationItem.title = "\(player.cfcID): \(player.name)"
            cfcID.setTitle( "\(player.cfcID)", for: .normal)
            cfcID.tintColor = UIColor(named: "HyperlinkBlue")
            name.text = player.name
            if(player.expiry.isEmpty) {
                cfcExpiry.text = String(localized: "Expired/Unregistered")
                cfcExpiry.textColor = UIColor(named: "CFCRed")
            }
            let dateFormatter = DateFormatter()
            dateFormatter.locale = Locale(identifier: "en_US")
            dateFormatter.dateFormat = "yyyy-MM-dd"
            let exp = dateFormatter.date(from: player.expiry)
            if exp == nil { //No expiry field was in the JSON means LIFE MEMBER
                cfcExpiry.text = String(localized: "No Expiry")
                cfcExpiry.textColor = UIColor(named: "AllGoodGreen")
            }
            else {
                let today = dateFormatter.date(from: dateFormatter.string(from: Date()))!
                if(exp! < today) {
                    cfcExpiry.text = String(localized: "Expired:").appending(player.expiry)
                    cfcExpiry.textColor = UIColor(named: "CFCRed")
                }
                else if(Calendar.current.component(.year, from: exp!) >= Calendar.current.component(.year, from: today) + 5) {
                    cfcExpiry.text = String(localized: "No Expiry")
                }
            }
            cityProv.text = player.cityProv
            if(player.fideID == 0) {
                fideID.setTitle( String(localized: "Unregistered"), for: .normal)
                fideID.tintColor = UIColor(named: "CFCRed")
            }
            else {
                fideID.setTitle( "\(player.fideID)", for: .normal)
                fideID.tintColor = UIColor(named: "HyperlinkBlue")
            }
            if(player.regular == 0) {
                regular.text = String(localized: "Unrated")
                regular.textColor = UIColor(named: "CFCRed")
            }
            else {
                regular.text = "\(player.regular)"
            }
            if(player.quick == 0) {
                quick.text = String(localized: "Unrated")
                quick.textColor = UIColor(named: "CFCRed")
            }
            else {
                quick.text = "\(player.quick)"
            }
            lastUpdated.text = player.updated!
        }
    }
    
    func initializeProfile(_ player: Player) {
        profile = player;
    }
    
    @IBAction func openCFCProfile() {
        var urlString: String
        if(Locale.current.language.languageCode?.identifier.lowercased() == "fr") {
            urlString = "https://chess.ca/fr/ratings/p/?id=\(profile!.cfcID)"
        }
        else {
            urlString = "https://chess.ca/en/ratings/p/?id=\(profile!.cfcID)"
        }
        performSegue(withIdentifier: "ShowWebProfileView", sender: urlString)
    }
    
    @IBAction func openFIDEProfile() {
        if let player = profile {
            if(player.fideID == 0) {
                let alert = UIAlertController(title: String(localized: "Player Not FIDE Registered"), message: String(localized: "This player is not FIDE registered."), preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: String(localized: "Dismiss"), style: .default))
                present(alert, animated: true)
            }
            else {
                let urlString = "https://ratings.fide.com/profile/\(profile!.fideID)"
                    performSegue(withIdentifier: "ShowWebProfileView", sender: urlString)
            }
        }
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "ShowWebProfileView") {
            let urlString = sender as! String
            let profileString = if (urlString.contains("fide")) {
                "FIDE"
            }
            else {
                "CFC"
            }
            let webVC = segue.destination as! WebProfileViewController
            webVC.initializeURLString(urlString)
            webVC.initializeProfileString(profileString)
        }
    }
    
}
