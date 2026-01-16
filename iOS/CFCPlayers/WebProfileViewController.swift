//
//  WebProfileViewController.swift
//  CFCPlayers
//
//  Created by Johnny Console on 2023-07-31.
//

import UIKit
import WebKit

class WebProfileViewController: UIViewController, WKNavigationDelegate {
    
    let FOOTER_HEIGHT: CGFloat = 55.0
    
    var webView : WKWebView = WKWebView()
    var footerView : UIView = UIView()
    var forward = UIButton(type: UIButton.ButtonType.custom)
    var back = UIButton(type: UIButton.ButtonType.custom)
    var urlString: String?
    var profileString: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = String(localized: LocalizedStringResource(stringLiteral: "Player's " + profileString! + " Profile"))
        webView.allowsBackForwardNavigationGestures = true
        view.backgroundColor = UIColor.white
        footerView.backgroundColor = UIColor.white
        
        forward.setImage(UIImage(systemName: "chevron.right"), for: .normal)
        forward.addTarget(self, action: #selector(forward(_:)), for: .touchUpInside)
        back.setImage(UIImage(systemName: "chevron.left"), for: .normal)
        back.addTarget(self, action: #selector(back(_:)), for: .touchUpInside)
        
        view.addSubview(webView)
        view.addSubview(footerView)
        view.addSubview(back)
        view.addSubview(forward)
        
        webView.allowsBackForwardNavigationGestures = true
        webView.navigationDelegate = self
        webView.load(URLRequest(url: NSURL(string: urlString!)! as URL))
    }
    
    override func viewWillLayoutSubviews() {
        let statusBarHeight = view.window!.windowScene!.statusBarManager!.statusBarFrame.height
        webView.frame = CGRect(origin: CGPoint(x: 0, y: statusBarHeight), size: CGSize(width: view.frame.size.width, height: view.frame.size.height + FOOTER_HEIGHT))
        
        footerView.frame = CGRect(origin: CGPoint(x: 0, y: view.frame.size.height - FOOTER_HEIGHT), size: CGSize(width: view.frame.size.width, height: FOOTER_HEIGHT))

        back.frame = CGRect(origin: CGPoint(x: 10, y: view.frame.size.height - FOOTER_HEIGHT), size: CGSize(width: FOOTER_HEIGHT, height: FOOTER_HEIGHT))
        
                
        forward.frame = CGRect(origin: CGPoint(x: 20 + FOOTER_HEIGHT, y: view.frame.size.height - FOOTER_HEIGHT), size: CGSize(width: FOOTER_HEIGHT, height: FOOTER_HEIGHT))
    }
    
    func initializeURLString(_ urlString: String) {
        self.urlString = urlString
    }
    
    func initializeProfileString(_ profileString: String) {
        self.profileString = profileString
    }

    @objc func back(_ sender: Any?) {
        if(webView.canGoBack) {
            webView.goBack()
        }
    }
    
    @objc func forward(_ sender: Any?) {
        if(webView.canGoForward) {
            webView.goForward()
        }
    }
}
