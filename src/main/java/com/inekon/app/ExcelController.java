package com.inekon.app;

import static com.inekon.app.CalculationBean.KEY_LEFT;
import static com.inekon.app.CalculationBean.KEY_RIGHT;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inekon.app.ShoppingCartBean.VersionBean;

@RestController
public class ExcelController {

	/** class name for the logger */
	private static final String LOG_CLASS = ExcelController.class.getName();

	/** logging level */
	private static final Level LOG_LEVEL = Level.FINER;

	/** class logger */
	private static final Logger LOGGER = Logger.getLogger(LOG_CLASS);

	/**
	 * Excel file used as a template for the computations. For auditing purposes
	 * we will copy the file into each executed computation, so we can validate.
	 */
	private static final File TEMPLATE_FILE = new File("dirk.xls");

	/**
	 * The initial entry point into our computations
	 * 
	 * @return the cart
	 */
	private final ShoppingCartListBean getShoppingCartListBean() {
		return new ShoppingCartListBean(TEMPLATE_FILE);
	}

	/**
	 * Constructs a new cart
	 * 
	 * @param aTitle
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = { "/createShoppingCart" }, method = { RequestMethod.PUT })
	public ShoppingCartRefBean createShoppingCart(@RequestBody String aTitle) throws IOException {
		// the list
		final ShoppingCartListBean list = getShoppingCartListBean();
		final ShoppingCartBean bean = list.createNewShoppingCart();
		bean.setTitle(aTitle);
		bean.saveProperties();
		// returns the new cart
		return new ShoppingCartRefBean(bean);
	}

	@RequestMapping("/shoppingCarts")
	public ShoppingCartListResultBean getShoppingCarts() {
		return new ShoppingCartListResultBean(getShoppingCartListBean());
	}

	@RequestMapping("/calc")
	public CalculationResult doCalculation(@RequestParam(value = "left", defaultValue = "1") int aLeft,
			@RequestParam(value = "right", defaultValue = "2") int aRight) throws IOException {
		// logging support
		final String LOG_METHOD = "doCalculation()";
		final boolean bIsLogging = LOGGER.isLoggable(LOG_LEVEL);
		if (bIsLogging) {
			LOGGER.entering(LOG_CLASS, LOG_METHOD);
		}
		/**
		 * Conceptually we perform each computation as part of a "shopping cart"
		 * . This cart has an ID and customers could be charged for it.
		 */
		final ShoppingCartListBean list = getShoppingCartListBean();
		final ShoppingCartBean cart = list.createNewShoppingCart();
		/**
		 * This is the bean that executes the actual calculation
		 */
		final CalculationBean calc = new CalculationBean(cart);
		/**
		 * Produce the input map from the input parameters. We add this extra
		 * level of indirection, since potentially the input to the REST service
		 * is not identical to the input to the bean
		 */
		final Map<String, String> input = new HashMap<String, String>();
		input.put(KEY_LEFT, Integer.toString(aLeft));
		input.put(KEY_RIGHT, Integer.toString(aRight));
		/**
		 * The result of the calculation is a new version bean, represented by a
		 * new folder inside the shopping cart structure. That way, each
		 * computation can be monitored and recreated.
		 */
		final VersionBean version = calc.doCalculation(input);
		// serve back the result
		final CalculationResult result = new CalculationResult(version);
		// exit trace
		if (bIsLogging) {
			LOGGER.exiting(LOG_CLASS, LOG_METHOD);
		}
		// the data
		return result;
	}
}
