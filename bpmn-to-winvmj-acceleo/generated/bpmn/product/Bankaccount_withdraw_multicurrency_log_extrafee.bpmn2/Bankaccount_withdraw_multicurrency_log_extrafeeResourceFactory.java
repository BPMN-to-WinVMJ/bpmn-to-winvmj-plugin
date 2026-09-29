// @generated from Bankaccount_withdraw_multicurrency_log_extrafee.bpmn2

package ;

import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import java.util.logging.Logger;
import java.lang.reflect.Constructor;
import .core.resource.Bankaccount_withdraw_multicurrency_log_extrafeeResourceImpl;

public abstract class Bankaccount_withdraw_multicurrency_log_extrafeeResourceFactory {
	private static final Logger LOGGER = Logger.getLogger(Bankaccount_withdraw_multicurrency_log_extrafeeResourceFactory.class.getName());

    public Bankaccount_withdraw_multicurrency_log_extrafeeResourceFactory()
    {

    }

    public static Bankaccount_withdraw_multicurrency_log_extrafeeResourceImpl createResource(String fullyQualifiedName, Object ... base)
    {
    	Bankaccount_withdraw_multicurrency_log_extrafeeResourceImpl record = null;
        if(true)
        {
        try {
            Class<?> clz = Class.forName(fullyQualifiedName);
            Constructor<?> constructor = clz.getDeclaredConstructors()[0];
            System.out.println(constructor.toString());
            record = (Bankaccount_withdraw_multicurrency_log_extrafeeResourceImpl) constructor.newInstance(base);
        } 
        catch (IllegalArgumentException e)
        {
            LOGGER.severe("Failed to create instance of Account.");
            LOGGER.severe("Given FQN: " + fullyQualifiedName);
            LOGGER.severe("Failed to run: Check your constructor argument");
            System.exit(20);
        }
        catch (ClassCastException e)
        {   LOGGER.severe("Failed to create instance of Account.");
            LOGGER.severe("Given FQN: " + fullyQualifiedName);
            LOGGER.severe("Failed to cast the object");
            System.exit(30);
        }
        catch (ClassNotFoundException e)
        {
            LOGGER.severe("Failed to create instance of Account.");
            LOGGER.severe("Given FQN: " + fullyQualifiedName);
            LOGGER.severe("Decorator can't be applied to the object");
            System.exit(40);
        }
        catch (Exception e)
        {
            LOGGER.severe("Failed to create instance of Account.");
            LOGGER.severe("Given FQN: " + fullyQualifiedName);
            System.exit(50);
        }
        }
        else
        {
            System.out.println("Config Fail");
            System.exit(10);
        }
        return record;
    }
}
