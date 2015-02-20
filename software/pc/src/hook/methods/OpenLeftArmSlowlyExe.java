package hook.methods;
import robot.Robot;
import strategie.GameState;
import enums.ActuatorOrder;
import exceptions.serial.SerialConnexionException;
import hook.Executable;

/**
 * @author theo
 * Classe à utiliser par un hook permettant d'ouvrir un bras lentement
 *
 */

public class OpenLeftArmSlowlyExe implements Executable
{
   
	 public OpenLeftArmSlowlyExe()
     {
			System.out.println("Hook d'ouverture du bras gauche lentement");
     }
	
    @Override
    public boolean execute(GameState<Robot> stateToConsider)
    {
    	try 
    	{
			System.out.println("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") au lancé du hook");
			stateToConsider.robot.useActuator(ActuatorOrder.ARM_LEFT_OPEN_SLOW, false);
			System.out.println("en position ("+stateToConsider.robot.getPosition().x+", "+stateToConsider.robot.getPosition().y+") après le lancé du hook");
		} 
    	catch (SerialConnexionException e) 
    	{
			e.printStackTrace();
		}
		return false;
    }
        
}
