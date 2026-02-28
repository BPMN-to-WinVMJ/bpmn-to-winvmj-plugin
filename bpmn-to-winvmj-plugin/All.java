package paymentgateway.product.all;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import vmj.routing.route.VMJServer;
import vmj.routing.route.Router;
import vmj.hibernate.integrator.HibernateUtil;
import org.hibernate.cfg.Configuration;

import vmj.auth.model.UserResourceFactory;
import vmj.auth.model.RoleResourceFactory;
import vmj.auth.model.core.UserResource;
import vmj.auth.model.core.RoleResource;

import paymentgateway.disbursement.DisbursementResourceFactory;
import paymentgateway.disbursement.core.DisbursementResource;
import paymentgateway.disbursement.DisbursementServiceFactory;
import paymentgateway.disbursement.core.DisbursementService;
import paymentgateway.payment.PaymentResourceFactory;
import paymentgateway.payment.core.PaymentResource;
import paymentgateway.payment.PaymentServiceFactory;
import paymentgateway.payment.core.PaymentService;

public class All {

	public static void main(String[] args) {

		// get hostAddress and portnum from env var
        // ex:
        // AMANAH_HOST_BE --> "localhost"
        // AMANAH_PORT_BE --> 7776
		String hostAddress= getEnvVariableHostAddress("AMANAH_HOST_BE");
        int portNum = getEnvVariablePortNumber("AMANAH_PORT_BE");
        activateServer(hostAddress, portNum);

		Configuration configuration = new Configuration();
		// panggil setter setelah membuat object dari kelas Configuration
        // ex:
        // AMANAH_DB_URL --> jdbc:postgresql://localhost:5432/superorg
        // AMANAH_DB_USERNAME --> postgres
        // AMANAH_DB_PASSWORD --> postgres123
		setDBProperties("AMANAH_DB_URL", "url", configuration);
        setDBProperties("AMANAH_DB_USERNAME", "username", configuration);
        setDBProperties("AMANAH_DB_PASSWORD","password", configuration);

		configuration.addAnnotatedClass(vmj.auth.model.core.Role.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.RoleComponent.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.RoleDecorator.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.RoleImpl.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.UserRole.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.UserRoleComponent.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.UserRoleDecorator.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.UserRoleImpl.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.User.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.UserComponent.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.UserDecorator.class);
        configuration.addAnnotatedClass(vmj.auth.model.core.UserImpl.class);
        configuration.addAnnotatedClass(vmj.auth.model.passworded.UserImpl.class);

		configuration.addAnnotatedClass(paymentgateway.disbursement.core.Disbursement.class);
		configuration.addAnnotatedClass(paymentgateway.disbursement.core.DisbursementComponent.class);
		configuration.addAnnotatedClass(paymentgateway.disbursement.core.DisbursementDecorator.class);
		configuration.addAnnotatedClass(paymentgateway.disbursement.core.DisbursementImpl.class);
		configuration.addAnnotatedClass(paymentgateway.disbursement.international.InternationalImpl.class);
		configuration.addAnnotatedClass(paymentgateway.disbursement.special.SpecialImpl.class);
		configuration.addAnnotatedClass(paymentgateway.disbursement.agent.AgentImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.core.Payment.class);
		configuration.addAnnotatedClass(paymentgateway.payment.core.PaymentComponent.class);
		configuration.addAnnotatedClass(paymentgateway.payment.core.PaymentDecorator.class);
		configuration.addAnnotatedClass(paymentgateway.payment.core.PaymentImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.creditcard.PaymentImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.debitcard.PaymentImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.ewallet.EWalletImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.invoice.PaymentImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.paymentlink.PaymentLinkImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.paymentrouting.PaymentImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.retailoutlet.RetailOutletImpl.class);
		configuration.addAnnotatedClass(paymentgateway.payment.virtualaccount.VirtualAccountImpl.class);

		Map<String, Object> featureModelMappings = mappingFeatureModel();
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, Map<String, String[]>>>(){}.getType();
        String convertedFeatureModelMappings = gson.toJson(featureModelMappings, type);
		
        configuration.setProperty("feature.model.mappings", convertedFeatureModelMappings);
		configuration.buildMappings();
		HibernateUtil.buildSessionFactory(configuration);

		createObjectsAndBindEndPoints();
	}

	public static void activateServer(String hostName, int portNumber) {
		VMJServer vmjServer = VMJServer.getInstance(hostName, portNumber);
		try {
			vmjServer.startServerGeneric();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void createObjectsAndBindEndPoints() {
		System.out.println("== CREATING OBJECTS AND BINDING ENDPOINTS ==");
		UserResource userResource = UserResourceFactory
            .createUserResource("vmj.auth.model.core.UserResourceImpl"
			);

		RoleResource roleResource = RoleResourceFactory
        	.createRoleResource("vmj.auth.model.core.RoleResourceImpl"
			);
        
        UserResource userPasswordedResource = UserResourceFactory
	        .createUserResource("vmj.auth.model.passworded.UserResourceImpl"
			,
		    UserResourceFactory.createUserResource("vmj.auth.model.core.UserResourceImpl"));

        DisbursementService disbursementDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.core.DisbursementServiceImpl"
            	);		

        DisbursementResource disbursementDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.core.DisbursementResourceImpl"
                );
			
        DisbursementService internationalDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.international.DisbursementServiceImpl"
            	, disbursementDisbursementService);		

        DisbursementResource internationalDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.international.DisbursementResourceImpl"
                , disbursementDisbursementResource, disbursementDisbursementService);
			
        DisbursementService internationaldisbursementvalidatorDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.internationaldisbursementvalidator.DisbursementServiceImpl"
            	, internationalDisbursementService);		

        DisbursementResource internationaldisbursementvalidatorDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.internationaldisbursementvalidator.DisbursementResourceImpl"
                , internationalDisbursementResource, internationalDisbursementService);
			
        DisbursementService exchangerateDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.exchangerate.DisbursementServiceImpl"
            	, disbursementDisbursementService);		

        DisbursementResource exchangerateDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.exchangerate.DisbursementResourceImpl"
                , disbursementDisbursementResource, disbursementDisbursementService);
			
        DisbursementService specialDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.special.DisbursementServiceImpl"
            	, disbursementDisbursementService);		

        DisbursementResource specialDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.special.DisbursementResourceImpl"
                , disbursementDisbursementResource, disbursementDisbursementService);
			
        DisbursementService domesticdisbursementvalidatorDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.domesticdisbursementvalidator.DisbursementServiceImpl"
            	, specialDisbursementService);		

        DisbursementResource domesticdisbursementvalidatorDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.domesticdisbursementvalidator.DisbursementResourceImpl"
                , specialDisbursementResource, specialDisbursementService);
			
        DisbursementService internationaldisbursementvalidatorDisbursement2Service = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.internationaldisbursementvalidator.DisbursementServiceImpl"
            	, domesticdisbursementvalidatorDisbursementService);		

        DisbursementResource internationaldisbursementvalidatorDisbursement2Resource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.internationaldisbursementvalidator.DisbursementResourceImpl"
                , domesticdisbursementvalidatorDisbursementResource, domesticdisbursementvalidatorDisbursementService);
			
        DisbursementService fixedcurrencyDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.fixedcurrency.DisbursementServiceImpl"
            	, disbursementDisbursementService);		

        DisbursementResource fixedcurrencyDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.fixedcurrency.DisbursementResourceImpl"
                , disbursementDisbursementResource, disbursementDisbursementService);
			
        DisbursementService agentDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.agent.DisbursementServiceImpl"
            	, disbursementDisbursementService);		

        DisbursementResource agentDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.agent.DisbursementResourceImpl"
                , disbursementDisbursementResource, disbursementDisbursementService);
			
        DisbursementService domesticdisbursementvalidatorDisbursement2Service = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.domesticdisbursementvalidator.DisbursementServiceImpl"
            	, agentDisbursementService);		

        DisbursementResource domesticdisbursementvalidatorDisbursement2Resource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.domesticdisbursementvalidator.DisbursementResourceImpl"
                , agentDisbursementResource, agentDisbursementService);
			
        DisbursementService agentdisbursementDisbursementService = DisbursementServiceFactory
            .createDisbursementService("paymentgateway.disbursement.agentdisbursement.DisbursementServiceImpl"
            	, disbursementDisbursementService);		

        DisbursementResource agentdisbursementDisbursementResource = DisbursementResourceFactory
            .createDisbursementResource("paymentgateway.disbursement.agentdisbursement.DisbursementResourceImpl"
                , disbursementDisbursementResource, disbursementDisbursementService);
			
        PaymentService paymentPaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.core.PaymentServiceImpl"
            	);		

        PaymentResource paymentPaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.core.PaymentResourceImpl"
                );
			
        PaymentService creditcardPaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.creditcard.PaymentServiceImpl"
            	, paymentPaymentService);		

        PaymentResource creditcardPaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.creditcard.PaymentResourceImpl"
                , paymentPaymentResource, paymentPaymentService);
			
        PaymentService debitcardPaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.debitcard.PaymentServiceImpl"
            	, paymentPaymentService);		

        PaymentResource debitcardPaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.debitcard.PaymentResourceImpl"
                , paymentPaymentResource, paymentPaymentService);
			
        PaymentService ewalletPaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.ewallet.PaymentServiceImpl"
            	, paymentPaymentService);		

        PaymentResource ewalletPaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.ewallet.PaymentResourceImpl"
                , paymentPaymentResource, paymentPaymentService);
			
        PaymentService invoicePaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.invoice.PaymentServiceImpl"
            	, paymentPaymentService);		

        PaymentResource invoicePaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.invoice.PaymentResourceImpl"
                , paymentPaymentResource, paymentPaymentService);
			
        PaymentService paymentlinkPaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.paymentlink.PaymentServiceImpl"
            	, paymentPaymentService);		

        PaymentResource paymentlinkPaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.paymentlink.PaymentResourceImpl"
                , paymentPaymentResource, paymentPaymentService);
			
        PaymentService paymentroutingPaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.paymentrouting.PaymentServiceImpl"
            	, paymentPaymentService);		

        PaymentResource paymentroutingPaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.paymentrouting.PaymentResourceImpl"
                , paymentPaymentResource, paymentPaymentService);
			
        PaymentService retailoutletPaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.retailoutlet.PaymentServiceImpl"
            	, paymentPaymentService);		

        PaymentResource retailoutletPaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.retailoutlet.PaymentResourceImpl"
                , paymentPaymentResource, paymentPaymentService);
			
        PaymentService virtualaccountPaymentService = PaymentServiceFactory
            .createPaymentService("paymentgateway.payment.virtualaccount.PaymentServiceImpl"
            	, paymentPaymentService);		

        PaymentResource virtualaccountPaymentResource = PaymentResourceFactory
            .createPaymentResource("paymentgateway.payment.virtualaccount.PaymentResourceImpl"
                , paymentPaymentResource, paymentPaymentService);
			

		System.out.println("virtualaccountPaymentResource endpoints binding");
		Router.route(virtualaccountPaymentResource);
		
		System.out.println("virtualaccountPaymentService endpoints binding");
		Router.route(virtualaccountPaymentService);
		
		System.out.println("retailoutletPaymentResource endpoints binding");
		Router.route(retailoutletPaymentResource);
		
		System.out.println("retailoutletPaymentService endpoints binding");
		Router.route(retailoutletPaymentService);
		
		System.out.println("paymentroutingPaymentResource endpoints binding");
		Router.route(paymentroutingPaymentResource);
		
		System.out.println("paymentroutingPaymentService endpoints binding");
		Router.route(paymentroutingPaymentService);
		
		System.out.println("paymentlinkPaymentResource endpoints binding");
		Router.route(paymentlinkPaymentResource);
		
		System.out.println("paymentlinkPaymentService endpoints binding");
		Router.route(paymentlinkPaymentService);
		
		System.out.println("invoicePaymentResource endpoints binding");
		Router.route(invoicePaymentResource);
		
		System.out.println("invoicePaymentService endpoints binding");
		Router.route(invoicePaymentService);
		
		System.out.println("ewalletPaymentResource endpoints binding");
		Router.route(ewalletPaymentResource);
		
		System.out.println("ewalletPaymentService endpoints binding");
		Router.route(ewalletPaymentService);
		
		System.out.println("debitcardPaymentResource endpoints binding");
		Router.route(debitcardPaymentResource);
		
		System.out.println("debitcardPaymentService endpoints binding");
		Router.route(debitcardPaymentService);
		
		System.out.println("creditcardPaymentResource endpoints binding");
		Router.route(creditcardPaymentResource);
		
		System.out.println("creditcardPaymentService endpoints binding");
		Router.route(creditcardPaymentService);
		
		System.out.println("paymentPaymentResource endpoints binding");
		Router.route(paymentPaymentResource);
		
		System.out.println("paymentPaymentService endpoints binding");
		Router.route(paymentPaymentService);
		
		System.out.println("agentdisbursementDisbursementResource endpoints binding");
		Router.route(agentdisbursementDisbursementResource);
		
		System.out.println("agentdisbursementDisbursementService endpoints binding");
		Router.route(agentdisbursementDisbursementService);
		
		System.out.println("domesticdisbursementvalidatorDisbursement2Resource endpoints binding");
		Router.route(domesticdisbursementvalidatorDisbursement2Resource);
		
		System.out.println("domesticdisbursementvalidatorDisbursement2Service endpoints binding");
		Router.route(domesticdisbursementvalidatorDisbursement2Service);
		
		System.out.println("agentDisbursementResource endpoints binding");
		Router.route(agentDisbursementResource);
		
		System.out.println("agentDisbursementService endpoints binding");
		Router.route(agentDisbursementService);
		
		System.out.println("fixedcurrencyDisbursementResource endpoints binding");
		Router.route(fixedcurrencyDisbursementResource);
		
		System.out.println("fixedcurrencyDisbursementService endpoints binding");
		Router.route(fixedcurrencyDisbursementService);
		
		System.out.println("internationaldisbursementvalidatorDisbursement2Resource endpoints binding");
		Router.route(internationaldisbursementvalidatorDisbursement2Resource);
		
		System.out.println("internationaldisbursementvalidatorDisbursement2Service endpoints binding");
		Router.route(internationaldisbursementvalidatorDisbursement2Service);
		
		System.out.println("domesticdisbursementvalidatorDisbursementResource endpoints binding");
		Router.route(domesticdisbursementvalidatorDisbursementResource);
		
		System.out.println("domesticdisbursementvalidatorDisbursementService endpoints binding");
		Router.route(domesticdisbursementvalidatorDisbursementService);
		
		System.out.println("specialDisbursementResource endpoints binding");
		Router.route(specialDisbursementResource);
		
		System.out.println("specialDisbursementService endpoints binding");
		Router.route(specialDisbursementService);
		
		System.out.println("exchangerateDisbursementResource endpoints binding");
		Router.route(exchangerateDisbursementResource);
		
		System.out.println("exchangerateDisbursementService endpoints binding");
		Router.route(exchangerateDisbursementService);
		
		System.out.println("internationaldisbursementvalidatorDisbursementResource endpoints binding");
		Router.route(internationaldisbursementvalidatorDisbursementResource);
		
		System.out.println("internationaldisbursementvalidatorDisbursementService endpoints binding");
		Router.route(internationaldisbursementvalidatorDisbursementService);
		
		System.out.println("internationalDisbursementResource endpoints binding");
		Router.route(internationalDisbursementResource);
		
		System.out.println("internationalDisbursementService endpoints binding");
		Router.route(internationalDisbursementService);
		
		System.out.println("disbursementDisbursementResource endpoints binding");
		Router.route(disbursementDisbursementResource);
		
		System.out.println("disbursementDisbursementService endpoints binding");
		Router.route(disbursementDisbursementService);
		
		System.out.println("authResource endpoints binding");
		Router.route(userPasswordedResource);
		Router.route(roleResource);
		Router.route(userResource);
	}

	private static Map<String, Object> mappingFeatureModel() {
		Map<String, Object> featureModelMappings = new HashMap<>();

		featureModelMappings.put(
            paymentgateway.disbursement.core.DisbursementComponent.class.getName(),
			new HashMap<String, String[]>() {{
				put("components", new String[] {
					paymentgateway.disbursement.core.DisbursementComponent.class.getName()
				});
				put("deltas", new String[] {
					paymentgateway.disbursement.international.InternationalImpl.class.getName(),
					paymentgateway.disbursement.special.SpecialImpl.class.getName(),
					paymentgateway.disbursement.agent.AgentImpl.class.getName()
				});
			}}
        );

		featureModelMappings.put(
            paymentgateway.payment.core.PaymentComponent.class.getName(),
			new HashMap<String, String[]>() {{
				put("components", new String[] {
					paymentgateway.payment.core.PaymentComponent.class.getName()
				});
				put("deltas", new String[] {
					paymentgateway.payment.creditcard.PaymentImpl.class.getName(),
					paymentgateway.payment.debitcard.PaymentImpl.class.getName(),
					paymentgateway.payment.ewallet.EWalletImpl.class.getName(),
					paymentgateway.payment.invoice.PaymentImpl.class.getName(),
					paymentgateway.payment.paymentlink.PaymentLinkImpl.class.getName(),
					paymentgateway.payment.paymentrouting.PaymentImpl.class.getName(),
					paymentgateway.payment.retailoutlet.RetailOutletImpl.class.getName(),
					paymentgateway.payment.virtualaccount.VirtualAccountImpl.class.getName()
				});
			}}
        );

		return featureModelMappings;
	}

	public static void setDBProperties(String varname, String typeProp, Configuration configuration) {
		String varNameValue = System.getenv(varname);
		String propertyName = String.format("hibernate.connection.%s",typeProp);
		if (varNameValue != null) {
			configuration.setProperty(propertyName, varNameValue);
		} else {
			String hibernatePropertyVal = configuration.getProperty(propertyName);
			if (hibernatePropertyVal == null) {
				String error_message = String.format("Please check '%s' in your local environment variable or "
                	+ "'hibernate.connection.%s' in your 'hibernate.properties' file!", varname, typeProp);
            	System.out.println(error_message);
			}
		}
	}

	// if the env variable for server host is null, use localhost instead.
    public static String getEnvVariableHostAddress(String varname_host){
            String hostAddress = System.getenv(varname_host)  != null ? System.getenv(varname_host) : "localhost"; // Host
            return hostAddress;
    }

    // try if the environment variable for port number is null, use 7776 instead
    public static int getEnvVariablePortNumber(String varname_port){
            String portNum = System.getenv(varname_port)  != null? System.getenv(varname_port)  : "7776"; //PORT
            int portNumInt = Integer.parseInt(portNum);
            return portNumInt;
    }

}