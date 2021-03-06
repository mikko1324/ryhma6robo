package virallinenRobotti6;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.*;

public class Seuraaja implements Runnable {

	LineFollower lf;
	// Huom. ColorSensoria k�ytt�viss� roboteissa pit�� tehd� t�h�n tarvittavat
	// muutokset!!!
	LightSensor light = new LightSensor(SensorPort.S4);
	UltrasonicSensor ultra = new UltrasonicSensor(SensorPort.S2);
	Stopwatch stopwatch = new Stopwatch();
	private int aika;
	private int vaAika = 0;
	private int blackWhiteThreshold = 55;
	private int tMax = 60;
	private String lightValues = "";
	private boolean isRunning;

	// etaisyys
	boolean minDistReached = false;
	int MINDISTANCE = 300;

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public synchronized int getBlackWhiteThreshold() {
		return blackWhiteThreshold;
	}

	public synchronized void setBlackWhiteThreshold(int blackWhiteThreshold) {
		this.blackWhiteThreshold = blackWhiteThreshold;
	}

	public Seuraaja(LineFollower lf) {
		this.lf = lf;
		isRunning = true;
	}

	public void run() {
		light.setFloodlight(true);
		// LCD.drawString("Valo: ", 0, 0);
		// LCD.drawString("Paina vasen ", 0, 2);
		// LCD.drawString("aloittaaksesi", 0, 3);
		// LCD.drawString("tai yhdista!", 0, 4);

		while (!Button.LEFT.isPressed()) {
			LCD.drawString("Valitse puoli", 0, 0);
			LCD.drawInt(light.readValue(), 9, 1);

			int value = ultra.getDistance();
			LCD.drawString("Etaisyys:", 4, 3);
			LCD.drawInt(value, 4, 5);
		}

		if (Button.LEFT.isPressed()) {
			LCD.clear();
			lf.data.setPuoli(2);
			LCD.drawString("Vasen puoli", 0, 0);
		}

		if (Button.RIGHT.isPressed()) {
			LCD.clear();
			lf.data.setPuoli(1);
			LCD.drawString("Oikea puoli", 0, 0);
		}

		// 5 Sekunnin viive
		try {
			Thread.sleep(2000);
			LCD.drawString("Kierros alkaa 2 sekunnin kuluttua!", 0, 0);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		// Aseteetaan vaihde 1 eli viivanseuranta
		lf.moot.setVaihde(1);
		stopwatch.reset();

		// LCD.clear();
		// LCD.drawString("Ajossa", 0, 0);
		// LCD.drawString("Aika: ", 0, 2);
		// LCD.drawString("Valo: ", 0, 3);

		while (isRunning) {

			// aika = stopwatch.elapsed();
			LCD.drawInt(light.readValue(), 9, 1);
			int value = ultra.getDistance();
			LCD.drawString("Etaisyys:", 4, 3);
			LCD.drawInt(value, 4, 5);

			int vaihdepaalla = lf.moot.getVaihde();
			LCD.drawString("VAIHDE:", 6, 3);
			LCD.drawInt(vaihdepaalla, 6, 5);

			// LCD.drawInt(aika / 1000, 6, 2);
			// LCD.drawInt(light.getLightValue(), 6, 3);
			//
			// // Valoarvon tallennus 100ms v�lein
			// if (aika > vaAika + 100) {
			// lightValues = lightValues + light.getLightValue() + " ";
			// lf.data.setLightValues(lightValues);
			// vaAika = aika;
			// }
			//
			// // Haetaan valo ja teho arvot
			// blackWhiteThreshold = lf.data.getValo();
			// lf.moot.setPower(lf.data.getTeho());
			//
			// // Vasemman puolen seuraus

			if (lf.data.getPuoli() == 2 && lf.moot.getVaihde() == 1) {

				if (light.readValue() > blackWhiteThreshold
						&& light.readValue() < tMax) {
					// && lf.moot.getVaihde() == 1) {
					lf.moot.rightTurn(100, 0.9f);

				}

				if (light.readValue() > tMax) {
					// && lf.moot.getVaihde() == 1) {
					lf.moot.rightTurn(100, 0.2f);
				}

				if (light.readValue() < blackWhiteThreshold) {
					// && lf.moot.getVaihde() == 1) {
					lf.moot.leftTurn(100, 0.9f);
				}

				if (light.readValue() > 52 && light.readValue() < 60) {

					lf.moot.eteenpain(100);
				}

				if (light.readValue() < 43) {

					lf.moot.leftTurn(100, 0.2f);
				}

			}

			// Oikean puolen seuraus
			else if (lf.data.getPuoli() == 1 && lf.moot.getVaihde() == 1) {
				if (light.readValue() > blackWhiteThreshold
						&& light.readValue() < tMax) {

					lf.moot.leftTurn(100, 0.9f);

				}

				if (light.readValue() > tMax) {

					lf.moot.leftTurn(100, 0.2f);
				}

				if (light.readValue() < blackWhiteThreshold) {

					lf.moot.rightTurn(100, 0.9f);
				}

				if (light.readValue() > 52 && light.readValue() < 60) {

					lf.moot.eteenpain(100);
				}

				if (light.readValue() < 43) {

					lf.moot.rightTurn(100, 0.2f);
				}

			} else if (lf.moot.getVaihde() == 2) {
				if (lf.data.getPuoli() == 2) {
					lf.moot.rotateLeft(150, 420);
					lf.moot.setVaihde(3);
				} else {
					lf.moot.rotateRight(150, 420);
				}
			} else if (lf.moot.getVaihde() == 3) {
				lf.moot.eteenpain(200);
			}

			// // Paluu radalle v�ist�n j�lkeen
			// while (lf.moot.getVaihde() == 3
			// && light.getLightValue() > blackWhiteThreshold) {
			// lf.moot.forwardSlow();
			// }
			// if (lf.moot.getVaihde() == 3
			// && light.getLightValue() < blackWhiteThreshold) {
			// lf.moot.setVaihde(1);
			// }
			// // Lopetus
			// if (lf.moot.getVaihde() == 0) {
			// lf.moot.stop();
			// isRunning = false;
			// }

		}
		// LCD.clear();
		// LCD.drawString("Kierros ohi!", 0, 0);
		// LCD.drawString("Aika: ", 0, 2);
		// LCD.drawInt(aika / 1000, 6, 2);
		// LCD.drawString("sekuntia", 0, 3);
	}

}
