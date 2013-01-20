/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.*;

import anchovy.*;
import anchovy.Components.Valve;
import anchovy.Pair.Label;
import anchovy.io.*;

/**
 * 
 * @author andrei
 */
public class ParserTest {
	static Parser parser = null;
	static GameEngine engine = null;
	@BeforeClass
	public static void initParser() {
		
		parser = new Parser(engine = new GameEngine());
		Valve valve1 = new Valve("Valve 1");
		
		engine.addComponent(valve1);
		InfoPacket info = new InfoPacket();
		
		info.namedValues.add(new Pair<Boolean>(Label.psit, true));
		info.namedValues.add(new Pair<Double>(Label.OPFL, 12.34));
		
		try {
			engine.getPowerPlantComponent("Valve 1").takeInfo(info);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test method for {@link anchovy.io.Parser#parseCommand(java.lang.String, java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testParseCommand() throws Exception 
	{
		InfoPacket info = new InfoPacket();
		info.namedValues.add(new Pair<String>(Label.cNme, "Valve 1"));
		info.namedValues.add(new Pair<Boolean>(Label.psit, false));
		info.namedValues.add(new Pair<Double>(Label.OPFL, 12.34));
		parser.parseCommand("Valve 1", "close");
		assertTrue(engine.getPowerPlantComponent("Valve 1").getInfo().namedValues.contains(new Pair<Boolean>(Label.psit, false)));
		
	}

	/**
	 * Test method for {@link anchovy.io.Parser#parse(java.lang.String)}.
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testParse()  {
		
		assertEquals(parser.parse(""), "Invalid command");
		
		assertEquals(parser.parse("no-spaces-string"), "");
		
		//assertNotSame(parser.parse("load abc.fg"), "");
	}

}
