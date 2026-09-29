// @generated from Sequence.bpmn2

package ;

import id.ac.ui.cs.prices.winvmj.core.VMJExchange;
import java.util.logging.Logger;
import java.lang.reflect.Constructor;
import .core.resource.SequenceResourceImpl;

public abstract class SequenceResourceFactory {
	private static final Logger LOGGER = Logger.getLogger(SequenceResourceFactory.class.getName());

    public SequenceResourceFactory()
    {

    }

    public static SequenceResourceImpl createResource(String fullyQualifiedName, Object ... base)
    {
    	SequenceResourceImpl record = null;
        if(true)
        {
        try {
            Class<?> clz = Class.forName(fullyQualifiedName);
            Constructor<?> constructor = clz.getDeclaredConstructors()[0];
            System.out.println(constructor.toString());
            record = (SequenceResourceImpl) constructor.newInstance(base);
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
