package engine;


import base.AbstractEnvironment;
import engine.util.*;

import java.util.*;

public class RoadsEnv extends AbstractEnvironment {
    private static final int MIN_DIST_ALLOWED = 5;
    private static final int CAR_DETECTION_RANGE = 30;
    //private static final int SEM_DETECTION_RANGE = 30;

    /* list of roads */
    private final List<Road> roads;

    /* traffic lights */
    private final List<TrafficLight> trafficLights;

    /* cars situated in the environment */
    private final HashMap<String, CarAgentInfo> registeredCars;

    public RoadsEnv() {
        super("environment");
        this.registeredCars = new HashMap<>();
        this.trafficLights = new ArrayList<>();
        this.roads = new ArrayList<>();
    }

    @Override
    public void init() {
        for(var tl: trafficLights) {
            tl.init();
        }
    }

    @Override
    public void step(long timePassed) {
        for(var tl: trafficLights) {
            tl.step(timePassed);
        }
    }

    public Road createRoad(P2d p0, P2d p1) {
        Road r = new Road(p0, p1);
        this.roads.add(r);
        return r;
    }

    public TrafficLight createTrafficLight(P2d pos, TrafficLight.TrafficLightState initialState, int greenDuration, int yellowDuration, int redDuration) {
        TrafficLight tl = new TrafficLight(pos, initialState, greenDuration, yellowDuration, redDuration);
        this.trafficLights.add(tl);
        return tl;
    }

    @Override
    public Percept getCurrentPercepts(String agentId) {
        CarAgentInfo carInfo = registeredCars.get(agentId);
        double pos = carInfo.getPos();
        Road road = carInfo.getRoad();
        Optional<CarAgentInfo> nearestCar = getNearestCarInFront(road,pos);
        Optional<TrafficLightInfo> nearestSem = getNearestSemaphoreInFront(road,pos);

        return new CarPercept(pos, nearestCar, nearestSem);
    }
    private Optional<CarAgentInfo> getNearestCarInFront(Road road, double carPos){
        return
                registeredCars
                        .values()
                        .stream()
                        .filter((carInfo) -> carInfo.getRoad() == road)
                        .filter((carInfo) -> {
                            double dist = carInfo.getPos() - carPos;
                            return dist > 0 && dist <= CAR_DETECTION_RANGE;
                        })
                        .min((c1, c2) -> (int) Math.round(c1.getPos() - c2.getPos()));
    }
    private Optional<TrafficLightInfo> getNearestSemaphoreInFront(Road road, double carPos){
        return
                road.getTrafficLights()
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
    public void registerNewCar(CarAgent carAgent, Road road, double initialPos) {
        registeredCars.put(carAgent.getName(), new CarAgentInfo(carAgent, road, initialPos));
    }
    public List<CarAgentInfo> getAgentInfo() {
        return this.registeredCars.values().stream().toList();
    }

    public List<Road> getRoads(){
        return roads;
    }

    public List<TrafficLight> getTrafficLights(){
        return trafficLights;
    }
}
