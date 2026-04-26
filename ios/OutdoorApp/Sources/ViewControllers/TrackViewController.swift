import UIKit
import MapKit
import CoreLocation

class TrackViewController: UIViewController, MKMapViewDelegate {

    private let mapView = MKMapView()
    private let recordButton = UIButton()
    private let statusLabel = UILabel()
    private let statsStackView = UIStackView()
    private var routeOverlay: MKPolyline?

    private var isRecording = false
    private var locationManager: CLLocationManager?
    private var trackPoints: [CLLocation] = []

    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        setupLocation()
    }

    private func setupUI() {
        title = "轨迹"
        view.backgroundColor = UIColor(red: 248/255, green: 250/255, blue: 252/255, alpha: 1)

        navigationController?.navigationBar.prefersLargeTitles = true

        mapView.showsUserLocation = true
        mapView.showsCompass = true
        mapView.showsScale = true
        mapView.isUserInteractionEnabled = true
        mapView.delegate = self
        mapView.mapType = .standard
        mapView.isZoomEnabled = true
        mapView.isScrollEnabled = true
        mapView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(mapView)

        statusLabel.text = "点击开始记录轨迹"
        statusLabel.font = .systemFont(ofSize: 14)
        statusLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        statusLabel.textAlignment = .center
        statusLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(statusLabel)

        recordButton.setTitle("开始记录", for: .normal)
        recordButton.setTitleColor(.white, for: .normal)
        recordButton.backgroundColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        recordButton.layer.cornerRadius = 30
        recordButton.titleLabel?.font = .systemFont(ofSize: 16, weight: .semibold)
        recordButton.translatesAutoresizingMaskIntoConstraints = false
        recordButton.addTarget(self, action: #selector(toggleRecording), for: .touchUpInside)
        view.addSubview(recordButton)

        statsStackView.axis = .horizontal
        statsStackView.distribution = .fillEqually
        statsStackView.spacing = 16
        statsStackView.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(statsStackView)

        let distanceView = createStatView(value: "0.0", label: "公里")
        let durationView = createStatView(value: "00:00", label: "时长")
        let elevationView = createStatView(value: "0", label: "米爬升")
        statsStackView.addArrangedSubview(distanceView)
        statsStackView.addArrangedSubview(durationView)
        statsStackView.addArrangedSubview(elevationView)

        NSLayoutConstraint.activate([
            mapView.topAnchor.constraint(equalTo: view.safeAreaLayoutGuide.topAnchor),
            mapView.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            mapView.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            mapView.heightAnchor.constraint(equalToConstant: UIScreen.main.bounds.height * 0.45),

            statusLabel.topAnchor.constraint(equalTo: mapView.bottomAnchor, constant: 16),
            statusLabel.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20),
            statusLabel.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -20),

            recordButton.topAnchor.constraint(equalTo: statusLabel.bottomAnchor, constant: 20),
            recordButton.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            recordButton.widthAnchor.constraint(equalToConstant: 120),
            recordButton.heightAnchor.constraint(equalToConstant: 60),

            statsStackView.topAnchor.constraint(equalTo: recordButton.bottomAnchor, constant: 30),
            statsStackView.leftAnchor.constraint(equalTo: view.leftAnchor, constant: 20),
            statsStackView.rightAnchor.constraint(equalTo: view.rightAnchor, constant: -20),
            statsStackView.heightAnchor.constraint(equalToConstant: 80)
        ])
    }

    private func setupLocation() {
        locationManager = CLLocationManager()
        locationManager?.delegate = self
        locationManager?.desiredAccuracy = kCLLocationAccuracyBest
        locationManager?.requestWhenInUseAuthorization()
    }

    private func createStatView(value: String, label: String) -> UIView {
        let view = UIView()
        view.backgroundColor = .white
        view.layer.cornerRadius = 12

        let valueLabel = UILabel()
        valueLabel.text = value
        valueLabel.font = .systemFont(ofSize: 24, weight: .bold)
        valueLabel.textColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        valueLabel.textAlignment = .center
        valueLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(valueLabel)

        let titleLabel = UILabel()
        titleLabel.text = label
        titleLabel.font = .systemFont(ofSize: 12)
        titleLabel.textColor = UIColor(red: 100/255, green: 116/255, blue: 139/255, alpha: 1)
        titleLabel.textAlignment = .center
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(titleLabel)

        NSLayoutConstraint.activate([
            valueLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            valueLabel.centerYAnchor.constraint(equalTo: view.centerYAnchor, constant: -10),
            titleLabel.topAnchor.constraint(equalTo: valueLabel.bottomAnchor, constant: 4),
            titleLabel.centerXAnchor.constraint(equalTo: view.centerXAnchor)
        ])

        return view
    }

    @objc private func toggleRecording() {
        if isRecording {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private func startRecording() {
        isRecording = true
        recordButton.setTitle("停止记录", for: .normal)
        recordButton.backgroundColor = UIColor(red: 239/255, green: 68/255, blue: 68/255, alpha: 1)
        statusLabel.text = "正在记录轨迹..."
        locationManager?.startUpdatingLocation()
    }

    private func stopRecording() {
        isRecording = false
        recordButton.setTitle("开始记录", for: .normal)
        recordButton.backgroundColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
        statusLabel.text = "点击开始记录轨迹"
        locationManager?.stopUpdatingLocation()
    }
}

extension TrackViewController: CLLocationManagerDelegate {
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard isRecording else { return }
        for location in locations {
            trackPoints.append(location)
        }

        if trackPoints.count >= 2 {
            updateRouteOverlay()
            let lastLocation = trackPoints.last!
            let region = MKCoordinateRegion(center: lastLocation.coordinate, latitudinalMeters: 500, longitudinalMeters: 500)
            mapView.setRegion(region, animated: true)
        }

        statusLabel.text = "已记录 \(trackPoints.count) 个轨迹点"
    }

    private func updateRouteOverlay() {
        if let existing = routeOverlay {
            mapView.removeOverlay(existing)
        }

        let coordinates = trackPoints.map { $0.coordinate }
        routeOverlay = MKPolyline(coordinates: coordinates, count: coordinates.count)
        if let overlay = routeOverlay {
            mapView.addOverlay(overlay)
        }
    }

    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        if let polyline = overlay as? MKPolyline {
            let renderer = MKPolylineRenderer(polyline: polyline)
            renderer.strokeColor = UIColor(red: 16/255, green: 185/255, blue: 129/255, alpha: 1)
            renderer.lineWidth = 4
            return renderer
        }
        return MKOverlayRenderer(overlay: overlay)
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        switch manager.authorizationStatus {
        case .authorizedWhenInUse, .authorizedAlways:
            manager.startUpdatingLocation()
            if let location = manager.location {
                let region = MKCoordinateRegion(center: location.coordinate, latitudinalMeters: 1000, longitudinalMeters: 1000)
                mapView.setRegion(region, animated: true)
            }
        case .denied, .restricted:
            statusLabel.text = "请开启位置权限"
        case .notDetermined:
            break
        @unknown default:
            break
        }
    }
}
