package tests;

import static org.junit.Assert.*;

import controller.*;
import model.*;
import util.*;
import view.*;

import org.junit.Before;
import org.junit.Test;

public class RequirementsTests {

	@Before
	public void setUp() throws Exception {
	}

	/**
	 * While the turbine is turning the generator, the total amount of power produced by the power plant will increase proportionally to the amount of steam flowing round the system. 
	 * Test implemented by creating a very simple power plant configuration of a valve, turbine and generator.
	 * The valves output flow rate is set to 100 then the turbine and generator calculate(), getting the initial value of the electricity being output.
	 * The valves output flow rate is then set to 200 and calculates are performed again. 
	 * Then it is asserted that the output from the generator increased (due to the flow rate entering the turbine increasing thus increasing its RPM, thus generating more electricity). 
	 * 
	 */
	@Test
	public void TU01_SF08() {
		Turbine turbine = new Turbine("Turbine");
		Generator generator = new Generator("Generator");
		Valve valve = new Valve("Valve");
		
		valve.connectToInput(turbine);
		valve.connectToOutput(turbine);
		turbine.connectToInput(valve);
		turbine.connectToOutput(generator);
		turbine.connectToOutput(valve);
		generator.connectToInput(turbine);
		
		valve.setOuputFlowRate(50);
		turbine.setOuputFlowRate(50);
		valve.calculate();
		
		turbine.calculate();
		generator.calculate();
		
		
		Double outputOfGenerator = generator.getOutputFlowRate();
		
		valve.setOuputFlowRate(200);
		turbine.setOuputFlowRate(200);
		turbine.calculate();
		generator.calculate();
		
		
		assertTrue("Original: " + outputOfGenerator + ". New: " + generator.getOutputFlowRate(), outputOfGenerator < generator.getOutputFlowRate());
	}
	/**
	 * The hotter the reactor tank is, the higher the rate of steam production. No steam will be produced while the reactor temperature is below 100 degrees Celsius.
	 * Creates a basic power plant containing just a valve and a reactor. 
	 * The valves output flow rate is set to 50 so that the Reactor has something to use to calculate with. And other reactor values are initiallised.
	 * The the output flow rate of the reactor is calculated after three settings of the temperature (two over 100 and one below).
	 * Then the output flows are compared to make sure that higher temperature makes higher output rate and that the temperature being below 100 means no output.
	 */
	@Test
	public void TU01_SF09(){
		Reactor reactor = new Reactor("Reactor");
		Valve valve = new Valve("Valve");
		
		double[] reactorOutputFlow = {0,0,0};
		
		reactor.connectToInput(valve);
		reactor.connectToOutput(valve);
		valve.connectToInput(reactor);
		valve.connectToInput(reactor);
		valve.setOuputFlowRate(50);
		
		
		reactor.setControlRodLevel(50);
		reactor.setAmount(50);
		reactor.setTemperature(150);
		reactor.setWaterLevel(50);
		reactor.calculate();
	
		reactorOutputFlow[0] = reactor.getOutputFlowRate();
		
		reactor.setTemperature(125);
		reactor.setControlRodLevel(50);
		reactor.setWaterLevel(50);
		reactor.setAmount(50);
		reactor.calculate();
	
		reactorOutputFlow[1] = reactor.getOutputFlowRate();
		
		reactor.setTemperature(50);
		reactor.setControlRodLevel(50);
		reactor.setWaterLevel(50);
		reactor.setAmount(50);
		reactor.calculate();
	
		reactorOutputFlow[2] = reactor.getOutputFlowRate();
		
		assertTrue("" + reactorOutputFlow[0] + " " + reactorOutputFlow[1] + " " +reactorOutputFlow[2], reactorOutputFlow[0] > reactorOutputFlow[1] && reactorOutputFlow[2] == 0);
		
	}
	/**
	 * The higher the control rods are raised the higher the rate of increase of temperature in the reactor will be.
	 * A simple power plant is created with two components; a reactor and a valve.
	 * The values of these components are initialised, then the temperature of the reactor is read after a calculation is preformed after setting the control rods at various positions.
	 * These values are compared to assert than the higher the control rod level the higher the temperature.
	 */
	@Test
	public void TU03_SF10(){
		Reactor reactor = new Reactor("Reactor");
		Valve valve = new Valve("Valve");
		
		reactor.connectToInput(valve);
		reactor.connectToOutput(valve);
		valve.connectToInput(reactor);
		valve.connectToOutput(reactor);
		
		valve.setOuputFlowRate(50);
		
		reactor.setControlRodLevel(50);
		reactor.setWaterLevel(50);
		reactor.setAmount(50);
		reactor.calculate();
		
		Double temp = reactor.getTemperature();
		
		reactor.setControlRodLevel(70);
		
		reactor.calculate();
		
		Double temp1 = reactor.getTemperature();
		
		assertTrue("" + temp + " " + temp1, temp < temp1);
		
	}
	
	/**
	 * The pressure generated in the reactor tank is directly proportional to the amount of steam generated in the reactor, so the pressure should always increase with amount of steam in the reactor. 
	 * Creates a basic power plant with two components, a reactor and a valve. These two components are initialised then by changing the temperature the pressure is changed. After the temperature is changed, the reactor calculates and its pressure read.
	 * These pressures are compared to assert that lower temperature means lower pressure. This is due to higher temperatures producing more steam, which intern increases the pressure.
	 */
	@Test
	public void TU04_SF19(){
		Reactor reactor = new Reactor("Reactor");
		Valve valve = new Valve("Valve");
		

		reactor.connectToInput(valve);
		reactor.connectToOutput(valve);
		valve.connectToInput(reactor);
		valve.connectToInput(reactor);
		valve.setOuputFlowRate(50);
		
		reactor.setControlRodLevel(50);
		reactor.setAmount(50);
		reactor.setTemperature(120);
		reactor.setWaterLevel(50);
		reactor.calculate();
		
		Double pres1 = reactor.getPressure();
		
		reactor.setControlRodLevel(50);
		reactor.setAmount(50);
		reactor.setTemperature(150);
		reactor.setWaterLevel(50);
		reactor.calculate();
		
		Double pres2 = reactor.getPressure();
		
		assertTrue("" + pres1 + " " + pres2, pres1 < pres2);
		
	}
	
	
	
}
