import UIKit

class MainTabBarController: UITabBarController {

    override func viewDidLoad() {
        super.viewDidLoad()
        setupTabs()
        setupAppearance()
    }

    private func setupTabs() {
        let homeVC = UINavigationController(rootViewController: HomeViewController())
        homeVC.tabBarItem = UITabBarItem(title: "首页", image: UIImage(systemName: "house"), selectedImage: UIImage(systemName: "house.fill"))

        let trackVC = UINavigationController(rootViewController: TrackViewController())
        trackVC.tabBarItem = UITabBarItem(title: "轨迹", image: UIImage(systemName: "map"), selectedImage: UIImage(systemName: "map.fill"))

        let equipmentVC = UINavigationController(rootViewController: EquipmentViewController())
        equipmentVC.tabBarItem = UITabBarItem(title: "装备", image: UIImage(systemName: "bag"), selectedImage: UIImage(systemName: "bag.fill"))

        let profileVC = UINavigationController(rootViewController: ProfileViewController())
        profileVC.tabBarItem = UITabBarItem(title: "我的", image: UIImage(systemName: "person"), selectedImage: UIImage(systemName: "person.fill"))

        viewControllers = [homeVC, trackVC, equipmentVC, profileVC]
    }

    private func setupAppearance() {
        tabBar.tintColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        tabBar.backgroundColor = .systemBackground
    }
}
