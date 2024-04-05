package pcd.base;


import pcd.monitors.ReadWriteMonitor;
import pcd.monitors.ReadWriteMonitorImpl;
import pcd.engine.AbstractEnvironment;
import pcd.engine.Action;
import pcd.engine.Percept;

import java.util.*;

public class RoadsEnv extends AbstractEnvironment {

	private static final int MIN_DIST_ALLOWED = 5;
	private static final int CAR_DETECTION_RANGE = 30;

	/* list of roads */
	private final List<Road> roads;

	/* traffic lights */
	private final List<TrafficLight> trafficLights;

	/* cars situated in the environment */
	private final HashMap<String, CarAgentInfo> registeredCars;
	private final ReadWriteMonitor readWriteMonitor;

	public RoadsEnv() {
		super("traffic-env");
		registeredCars = new HashMap<>();
		trafficLights = new ArrayList<>();
		roads = new ArrayList<>();
		this.readWriteMonitor = new ReadWriteMonitorImpl();
	}

	@Override
	public void init() {
		for (var tl : trafficLights) {
			tl.init();
		}
	}

	@Override
	public void step(int dt) {
		for (var tl : trafficLights) {
			tl.step(dt);
		}
	}

	public void registerNewCar(CarAgent car, Road road, double pos) {
		registeredCars.put(car.getObjectId(), new CarAgentInfo(car, road, pos));
	}

	public Road createRoad(P2d p0, P2d p1) {
		Road r = new Road(p0, p1);
		this.roads.add(r);
		return r;
	}

	public TrafficLight createTrafficLight(P2d pos, TrafficLight.TrafficLightState initialState, int greenDuration,
			int yellowDuration, int redDuration) {
		TrafficLight tl = new TrafficLight(pos, initialState, greenDuration, yellowDuration, redDuration);
		this.trafficLights.add(tl);
		return tl;
	}

	@Override
	public Percept getCurrentPercepts(String agentId) {

		CarAgentInfo carInfo = registeredCars.get(agentId);
		double pos = carInfo.getPos();
		System.out.println(pos);
		Road road = carInfo.getRoad();
		Optional<CarAgentInfo> nearestCar = getNearestCarInFront(road, pos);
		Optional<TrafficLightInfo> nearestSem = getNearestSemaphoreInFront(road, pos);

		return new CarPercept(pos, nearestCar, nearestSem);
	}

	private Optional<CarAgentInfo> getNearestCarInFront(Road road, double carPos) {
		return registeredCars
				.values()
				.stream()
				.filter((carInfo) -> carInfo.getRoad() == road)
				.filter((carInfo) -> {
					double dist = carInfo.getPos() - carPos;
					return dist > 0 && dist <= (double) RoadsEnv.CAR_DETECTION_RANGE;
				})
				.min((c1, c2) -> (int) Math.round(c1.getPos() - c2.getPos()));
	}

	private Optional<TrafficLightInfo> getNearestSemaphoreInFront(Road road, double carPos) {
		return road.getTrafficLights()
				.stream()
				.filter((TrafficLightInfo tl) -> tl.roadPos() > carPos)
				.min((c1, c2) -> (int) Math.round(c1.roadPos() - c2.roadPos()));
	}

	@Override
	public void doAction(String agentId, Action act) {
        if (Objects.requireNonNull(act) instanceof MoveForward mv) {
            CarAgentInfo info = registeredCars.get(agentId);
            Road road = info.getRoad();
            Optional<CarAgentInfo> nearestCar = getNearestCarInFront(road, info.getPos());

            if (nearestCar.isPresent()) {
                double dist = nearestCar.get().getPos() - info.getPos();
                if (dist > mv.distance() + MIN_DIST_ALLOWED) {
                    info.updatePos(info.getPos() + mv.distance());
                }
            } else {
                info.updatePos(info.getPos() + mv.distance());
            }

            if (info.getPos() > road.getLen()) {
                info.updatePos(0);
            }
        }
	}

	public List<CarAgentInfo> getAgentInfo() {
		return this.registeredCars.values().stream().toList();
	}

	public List<Road> getRoads() {
		return roads;
	}

	public List<TrafficLight> getTrafficLights() {
		return trafficLights;
	}

	@Override
	public ReadWriteMonitor getReadWriteMonitor() {
		return this.readWriteMonitor;
	}
}
