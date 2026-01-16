//
//  ViewController.swift
//  CFCPlayers
//
//  Created by Johnny Console on 2023-07-30.
//

import UIKit

class PlayerCell: UITableViewCell {
    @IBOutlet weak var label: UILabel!
}

class PlayerSearchViewController: UIViewController, UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var loading: UIActivityIndicatorView!
    @IBOutlet weak var searchField: UISegmentedControl!
    @IBOutlet weak var searchButton: UIButton!
    @IBOutlet weak var searchText: UITextField!
    @IBOutlet weak var playerTable: UITableView!
    
    var players = [Player]()
    var searched = false

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let navbar = self.navigationController!.navigationBar
        let appearance = UINavigationBarAppearance()
        appearance.configureWithOpaqueBackground()
        appearance.backgroundColor = UIColor(named: "CFCRed")
        appearance.titleTextAttributes = [.foregroundColor: UIColor.white]
        navbar.scrollEdgeAppearance = appearance
        navbar.standardAppearance = appearance
        navbar.compactAppearance = appearance
        
        searchField.setTitle(String(localized: "CFC ID"), forSegmentAt: 0)
        searchField.setTitle(String(localized: "Last Name"), forSegmentAt: 1)
        searchField.setTitle(String(localized: "First Name"), forSegmentAt: 2)
        searchButton.setTitle(String(localized: "Search"), for: .normal)
    }

    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell = tableView.dequeueReusableCell(withIdentifier: "PlayerCell") as? PlayerCell
        if(cell == nil) {
            cell = PlayerCell(style: .default, reuseIdentifier: "PlayerCell")
        }
        if(players.count > 0) {
            cell!.label.text = "\(players[indexPath.row].cfcID): \(players[indexPath.row].name)"
        }
        else {
            cell!.label.text = String(localized: "No Players Found")
        }
        return cell!
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if(!searched) {
            return 0
        }
        else {
            return (players.count == 0 ? 1 : players.count)
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if(players.count == 0) {
            return
        }
        else {
            performSegue(withIdentifier: "ShowPlayerDetailsView", sender: players[indexPath.row])
        }
    }
    
    @IBAction func execSearch() {
        searchText.resignFirstResponder()
        searched = true
        if searchText.text == nil || searchText.text!.isEmpty {
            let controller = UIAlertController(title: String(localized: "Missing Criteria"), message: String(localized: "Please enter search criteria."), preferredStyle: .alert)
            controller.addAction(UIAlertAction(title: String(localized: "Dismiss"), style: .default))
            present(controller, animated: true, completion: nil)
        }
        else {
            let criteria = searchText.text!
            var urlString: String
            switch(searchField.selectedSegmentIndex) {
                case 0:
                    urlString = "https://server.chess.ca/api/player/v1/\(criteria)"
                case 1:
                    urlString = "https://server.chess.ca/api/player/v1/find?first=&last=\(criteria)*"
                default:
                    urlString = "https://server.chess.ca/api/player/v1/find?first=\(criteria)*&last="
            }
            players = [Player]()
            let field = searchField.selectedSegmentIndex
            loading.isHidden = false
            let url = NSURL(string: urlString)! as URL
            let request = URLRequest(url: url)
            let session = URLSession(configuration: URLSessionConfiguration.default)
            session.dataTask(with: request, completionHandler:
                { [self] (data, response, error)  in
                do {
                    let decoder = JSONDecoder()
                    if field == 0 {
                        let playerResponse = try decoder.decode(PlayerResponse.self, from: data!)
                        players.append(playerResponse.player)
                    }
                    else {
                        let playersResponse = try decoder.decode(PlayerArrayResponse.self, from: data!)
                        for p in playersResponse.players {
                            players.append(p)
                        }
                    }
                    DispatchQueue.main.async { [self] in
                        playerTable.reloadData()
                        loading.isHidden = true
                    }
                } catch {
                    DispatchQueue.main.async { [self] in
                        playerTable.reloadData()
                        loading.isHidden = true
                    }
                }
            }).resume()
        }
    }
    
    @IBAction func onSearchFieldChange(_ sender: UISegmentedControl) {
        searchText.resignFirstResponder()
        searchText.keyboardType = sender.selectedSegmentIndex == 0 ?
            .numberPad : .default
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        execSearch()
        return true
    }

    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "ShowPlayerDetailsView") {
            let detailsVC = segue.destination as! PlayerDetailsViewController
            detailsVC.initializeProfile(sender as! Player)
        }
    }
}

