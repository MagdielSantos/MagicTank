package mypackage;

import robocode.*;
import java.awt.Color;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import robocode.AdvancedRobot;

/**
 * MagicTank - a class By Magdiel Santos
 */
public class MagicTank extends AdvancedRobot {
    private static final double WALL_MARGIN = 50;  // Margem de segurança em relação às paredes
    private boolean moveAfterFire = false;

    public void run() {
        setColors(Color.black, Color.magenta, Color.magenta, Color.yellow, Color.magenta);

        while (true) {
           
            // Verifica se o radar ainda está travado no inimigo
            if (getRadarTurnRemaining() == 0.0) {
                setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
            }

			// Verifica a distância até a parede mais próxima em todas as direções
            double distanceToWall = Math.min(Math.min(getX(), getY()), Math.min(getBattleFieldWidth() - getX(), getBattleFieldHeight() - getY()));

            // Se estiver muito perto da parede, vire para longe dela
            if (distanceToWall < WALL_MARGIN) {
                double turnAngle = 90;  // Ângulo para girar em graus (pode ser ajustado)
                setTurnRight(turnAngle);
            }

            ahead(100);  // Move-se para frente

            execute();
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Calcula o ângulo até o inimigo
	double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
        
        // Calcula o quanto o radar precisa girar para travar no inimigo
	double radarTurn = Utils.normalRelativeAngle(angleToEnemy - getRadarHeadingRadians());
        
        // Calcula o quanto o canhão precisa girar para mirar no inimigo
	double gunTurn = Utils.normalRelativeAngle(angleToEnemy - getGunHeadingRadians());
       
 	// Calcula o quantogirar a mais para evitar problemas e ajusta a mira
	double extraTurn = Math.min(Math.atan(36.0 / e.getDistance()), Rules.RADAR_TURN_RATE_RADIANS);

        radarTurn += (radarTurn < 0 ? -extraTurn : extraTurn);
        gunTurn -= extraTurn;
        gunTurn += extraTurn;

        // Ajusta a mira do canhão e do radar
        setTurnGunRightRadians(gunTurn);
        setTurnRadarRightRadians(radarTurn);

        // Ajusta o poder do tiro com base na distância
        double bulletPower = Math.min(3, getEnergy() / 6);
        if (getGunHeat() == 0) { // Verifica se o canhão pode disparar
            fire(bulletPower);
        }
    }

    public void onHitWall(HitWallEvent e) {
        // Ao bater em uma parede, inverta a direção
        setBack(50);
    }

    public void onHitRobot(HitRobotEvent e) {
        // Ao bater em um robô, inverta a direção
        setBack(50);
    }
}
