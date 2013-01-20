package anchovy.Components;

import java.util.ArrayList;
import java.util.Iterator;

import anchovy.InfoPacket;
import anchovy.Pair;
import anchovy.Pair.Label;
/**
 * This is the representation of the Reactor within the power plant.
 * @author Harrison
 */
public class Reactor extends WaterComponent {
	private double pressure = 0.0; //---DEPRECATED--- pressure is now just a function of temp, Amnt, and Vlme
	private double controlRodLevel = 50.0;
	private double waterLevel = 50;// ---DEPRECATED---
	/**
	 * @see anchovy.Components.Component#Component(String)
	 */
	public Reactor(String name){
		super(name);
	}

	/** 
	 * @see anchovy.Components.Component#Component(String)
	 */
	public Reactor(String name, InfoPacket info){
		super(name, info);
		Pair<?> currentpair = null;
		Iterator<Pair<?>> pi = info.namedValues.iterator();
		Label currentlabel = null;
		while(pi.hasNext()){
			currentpair = pi.next();
			currentlabel = currentpair.getLabel();
			switch (currentlabel){
			case pres:
				pressure = (Double) currentpair.second();
				break;
			case coRL:
				controlRodLevel = (Double) currentpair.second();
				break;
			case wLvl:
				waterLevel = (Double) currentpair.second();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InfoPacket getInfo() {
		InfoPacket info = super.getInfo();
		info.namedValues.add(new Pair<Double>(Label.pres, pressure));
		info.namedValues.add(new Pair<Double>(Label.coRL, controlRodLevel));
		info.namedValues.add(new Pair<Double>(Label.wLvl, waterLevel));
		return info;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void calculate() {
		
		if(!super.isFailed()){
			super.setFailed(calculateFailed());
			double oldTemp = getTemperature();
			setTemperature(calculateTemperature());
			waterLevel = calculateWaterLevel();
			super.setOuputFlowRate(calculateOutputFlowRate());
		}
	}

	/** 
	 * {@inheritDoc}
	 * Reactors can fail for several different reasons.
	 * If the temperature or pressure are too high or the water level is too low.
	 * They also fail when they reach their randomly calculated fail time.
	 */
	@Override
	protected boolean calculateFailed() {
		if(super.getFailureTime() == 0){
			return true;
		}else if(getTemperature() > 300){
			return true;
		}else if(pressure > 200){
			return true;
		}else if(waterLevel <= 0){
			return true;
		}
		return false;
	}

	/**
	 * Calculate the temperature of the reactor.
	 * The temperature of the reactor depends on the Control Rods.
	 * When the are lowered, the reactor gradually cools, when they are raised the Reactor get hotter and hotter.
	 * @return The new Temperature of the reactor.
	 */
	protected double calculateTemperature(){
		//The temperature is affected by the level of the control rods, current temperature.
		//Higher control rod level the hotter it gets.
		double t = getTemperature();
		if(t > 100){
			t = t + t * ((controlRodLevel-50)/20); //If boiling lowering control rod level past 50% decreases temp otherwise it increases.
		}else{
			t = t + t * ((controlRodLevel-5)/20); //If not boiling then control rod increases temp unless fully down
		}
		return t;
	}
	/**
	 * Calculate the water level in the reactor.
	 * Water level = water level  + input flow rates - rate of steam production.
	 * @return The new water level in the reactor
	 *  ---DEPRECATED---
	 */
	protected double calculateWaterLevel(){
		//proportional to current water level + opfl and ipfl
		double inputFlowRate = 0;
		ArrayList<Component> inputs = super.getRecievesInputFrom();
		Iterator<Component> it = inputs.iterator();
		Component c = null;
		while(it.hasNext()){
			c = it.next();
			inputFlowRate += c.getOutputFlowRate();
		}
		return (inputFlowRate + waterLevel) - super.getOutputFlowRate();
	}

	/**
	 * {@inheritDoc}
	 * The output flow rate of the Reactor is proportional to the pressure within the reactor.
	 *  ---DEPRECATED---
	 */
	@Override
	protected double calculateOutputFlowRate(){
		// OPFL is proportional to the pressure. 
		return pressure / 2;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void takeInfo(InfoPacket info) throws Exception {
		super.takeSuperInfo(info);
		Iterator<Pair<?>> i = info.namedValues.iterator();
		Pair<?> pair = null;
		Label label = null;
		while(i.hasNext()){
			pair = i.next();
			label = pair.getLabel();
			switch (label){
			case temp:
				setTemperature((Double) pair.second());
				break;
			case pres:
				setPressure((Double) pair.second());
				break;
			case coRL:
				setControlRodLevel((Double) pair.second());
				break;
			case wLvl:
				setWaterLevel((Double) pair.second());
				break;
			default:
				// should this do anything by default?
			}
		}
	}

	/**
	 * @param pressure The pressure that the reactor will be at.
	 *  ---DEPRECATED---
	 */
	public void setPressure(double pressure) {
		this.pressure = pressure;
	}
	/**
	 * @return The level the the control rods inside the reactor at at.
	 */
	public double getControlRodLevel() {
		return controlRodLevel;
	}
	/**
	 * @param controlRodLevel The level that the control rods in the reactor will be at, max = 100, min = 0
	 */
	public void setControlRodLevel(double controlRodLevel) {
		if (controlRodLevel > 100)
			this.controlRodLevel =100;
		else if (controlRodLevel < 0)
			this.controlRodLevel = 0;
		else
			this.controlRodLevel = controlRodLevel;
	}

	/**
	 * @return The level of the water in the reactor.
	 *  ---DEPRECATED---
	 */
	public double getWaterLevel() {
		return waterLevel;
	}
	/**
	 * @param waterLevel The level that the water in the reactor will be at. Max = 100, min = 0;
	 *  ---DEPRECATED---
	 */
	public void setWaterLevel(double waterLevel) {
		if(waterLevel < 100 & waterLevel > 0)
			this.waterLevel = waterLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	public InfoPacket outputWater() {
		InfoPacket waterpack = new InfoPacket();
		if (getTemperature() < 100){
			waterpack.namedValues.add(new Pair<Double>(Pair.Label.Amnt, 0.0));
			waterpack.namedValues.add(new Pair<Double>(Pair.Label.temp, 0.0));
		}else{
			double packAmount = getPressure()/10;
			waterpack.namedValues.add(new Pair<Double>(Pair.Label.Amnt, packAmount));
			setAmount(getAmount() - packAmount);
			waterpack.namedValues.add(new Pair<Double>(Pair.Label.temp, getTemperature()));
		}
		return waterpack;
	}

	@Override
	public double maxInput() {
		return 1987654321;
	}

}
