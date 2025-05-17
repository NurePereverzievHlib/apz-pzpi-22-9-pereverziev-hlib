interface Observer {
void update(float
temperature);
}

class TemperatureSensor {
private List<Observer> observers = new ArrayList<>();
private float temperature;

void addObserver(Observer observer) {
observers.add(observer);

void notifyObservers() {
for (Observer observer : observers) {
observer.update(temperature);

void setTemperature(float temperature) {
this.temperature = temperature;
notifyObservers();

}


class TemperatureDisplay implements
Observer {
public void update(float temperature) {
System.out.println("TemnepaTypa:
+ temperature + "C");
}

}
