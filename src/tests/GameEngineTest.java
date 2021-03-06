package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import model.*;

import org.junit.Before;
import org.junit.Test;

import controller.*;

import util.InfoPacket;
import util.Pair;
import util.Pair.Label;


public class GameEngineTest {
	GameEngine gameEngine = null;
	Valve valve1 = null;
	ArrayList<InfoPacket> v1Info = null;
	
	/**
	 * Makes a basic power plant with one component (Valve 1) that is not connected to anything and has had not info assigned to it.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		gameEngine = new GameEngine();
		valve1 = new Valve("Valve 1");
		v1Info = new ArrayList<InfoPacket>();
		gameEngine.addComponent(valve1);
	}

	/**
	 * Test whether adding a component to the list of components in the power plant is sucsessful.
	 */
	@Test
	public void testAddComponent() {
		
		ArrayList<InfoPacket> info = gameEngine.getAllComponentInfo();
		v1Info.add(valve1.getInfo());
		assertTrue(v1Info.equals(info));
		
	}
	
	/**
	 * Tests whether connecting two components is successful and done in the correct way.
	 * @throws Exception 
	 */
	@Test
	public void testConnectComponents() throws Exception{
		gameEngine.connectComponentTo(valve1, valve1, true);
		
		Component c = gameEngine.getPowerPlantComponent("Valve 1");
		assertTrue(c.getInfo().namedValues.contains(new Pair<String>(Label.oPto, "Valve 1")));
		assertEquals(1, c.getOutputsTo().size());
		assertEquals(1, c.getRecievesInputFrom().size());	
		
	}
	/**
	 * Tests whether the game engine is able to find the correct component and assign info to it. 
	 * @throws Exception 
	 */
	@Test
	public void testAssignInfoToComponent() throws Exception{
		InfoPacket info = gameEngine.getAllComponentInfo().get(0);
	
		
		info.namedValues.add(new Pair<Boolean>(Label.psit, true));
		info.namedValues.add(new Pair<Double>(Label.OPFL, 12.34));


		info.namedValues.add(new Pair<Double>(Label.Amnt, 40.0));
		info.namedValues.add(new Pair<Double>(Label.Vlme, 0.0));
		
		try {
			gameEngine.assignInfoToComponent(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Component v1 = gameEngine.getPowerPlantComponent("Valve 1");
		info = new InfoPacket();
		info.namedValues.add(new Pair<String>(Label.cNme, "Valve 1"));
		info.namedValues.add(new Pair<Boolean>(Label.psit, true));
		info.namedValues.add(new Pair<Double>(Label.OPFL, 12.34));;
		info.namedValues.add(new Pair<Double>(Label.falT, v1.getFailureTime()));
		info.namedValues.add(new Pair<Double>(Label.Amnt, 40.0));
		info.namedValues.add(new Pair<Double>(Label.Vlme, 0.0));
		info.namedValues.add(new Pair<Double>(Label.temp, 50.0));// valves have temperature now to make my hairbrained schemes work - kieran 
		
		InfoPacket v1Info = v1.getInfo();
		assertTrue(v1Info.equals(info));
		
	}
	/**
	 * Tests whether the game engine correctly sets up a power plant given a list of infoPackets
	 * @throws Exception 
	 */
	@Test
	public void testSetupPowerPlantConfigureation() throws Exception{
		ArrayList<InfoPacket> infoList = new ArrayList<InfoPacket>();
		
		InfoPacket info1 = new InfoPacket();
		info1.namedValues.add(new Pair<String>(Label.cNme, "Valve 1"));
		info1.namedValues.add(new Pair<Boolean>(Label.psit, true));
		info1.namedValues.add(new Pair<Double>(Label.OPFL, 12.34));
		info1.namedValues.add(new Pair<String>(Label.rcIF, "Valve 2"));
		info1.namedValues.add(new Pair<String>(Label.oPto, "Valve 2"));
		info1.namedValues.add(new Pair<Double>(Label.Amnt, 40.0));
		info1.namedValues.add(new Pair<Double>(Label.Vlme, 0.0));
		info1.namedValues.add(new Pair<Double>(Label.temp, 50.0));// valves have temperature now to make my hairbrained schemes work - kieran 
		infoList.add(info1);
		
		InfoPacket info2 = new InfoPacket();
		info2.namedValues.add(new Pair<String>(Label.cNme, "Valve 2"));
		info2.namedValues.add(new Pair<Boolean>(Label.psit, true));
		info2.namedValues.add(new Pair<Double>(Label.OPFL, 12.34));
		info2.namedValues.add(new Pair<String>(Label.oPto, "Valve 1"));
		info2.namedValues.add(new Pair<String>(Label.rcIF, "Valve 1"));
		info2.namedValues.add(new Pair<Double>(Label.Amnt, 40.0));
		info2.namedValues.add(new Pair<Double>(Label.Vlme, 0.0));
		info2.namedValues.add(new Pair<Double>(Label.temp, 50.0));// valves have temperature now to make my hairbrained schemes work - kieran 
		
		infoList.add(info2);
		
		gameEngine.clearPowerPlant();
		assertTrue(gameEngine.getAllComponentInfo().isEmpty());
		
		gameEngine.setupPowerPlantConfiguration(infoList);
		info1.namedValues.add(new Pair<Double>(Label.falT, gameEngine.getPowerPlantComponent("Valve 1").getFailureTime()));
		info2.namedValues.add(new Pair<Double>(Label.falT, gameEngine.getPowerPlantComponent("Valve 2").getFailureTime()));
		
		infoList = new ArrayList<InfoPacket>();
		infoList.add(info1);
		infoList.add(info2);
		
		ArrayList<InfoPacket> allCompInfo = gameEngine.getAllComponentInfo();
		assertTrue(allCompInfo.equals(infoList));
	}
	
	/**
	 * Tests whether components are repaired correctly.
	 * @throws Exception 
	 */
	@Test
	public void testRepair() throws Exception{
		Component v1 = gameEngine.getPowerPlantComponent("Valve 1");
		v1.setFailed(true);
		assertTrue(v1.getFailed());
		
		gameEngine.repair(v1);
		assertTrue(!v1.getFailed());
	}
	
	/**
	 * Save the state of each of the components in the power plant, then load the same file in and test whether the infoPacket output was the same as the infoPacket read in.
	 * @throws Exception 
	 */
	@Test
	public void testSavingLoading() throws Exception
	{
		Valve v = (Valve) gameEngine.getPowerPlantComponent("Valve 1");
		v.setPosition(false);
		ArrayList<InfoPacket> allCompInfo = gameEngine.getAllComponentInfo();
		gameEngine.saveGameState(allCompInfo, "test");
		try{
			gameEngine.readfile("test");
		} catch(FileNotFoundException e)
		{
			assertTrue(false);
		}
		assertTrue(allCompInfo.equals(gameEngine.getAllComponentInfo()));
		
		File f = new File("saves/test.fg");
		f.delete();
	}
	/**
	 * Test whether the the game can search for and display the location of a particular game save.
	 * @throws Exception 
	 */
	@Test
	public void testShowAvailableSaves() throws Exception
	{
		Valve v = (Valve) gameEngine.getPowerPlantComponent("Valve 1");
		v.setPosition(false);
		gameEngine.saveGameState(gameEngine.getAllComponentInfo(), "show saves");
		
		
		assertTrue(gameEngine.findAvailableSaves().contains("show saves"));
		
		File f = new File("saves/show saves.fg");
		f.delete();
	}
}
