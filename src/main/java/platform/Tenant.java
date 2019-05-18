package platform;

import com.holonplatform.core.Validator;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertySet;

/**
 * Product model
 */
public interface Tenant {

	static final PathProperty<Long> CEP = PathProperty.create("cep", Long.class);

	static final PathProperty<String> CIDADE = PathProperty.create("cidade", String.class);

	static final PathProperty<String> PDV = PathProperty.create("pdv", String.class);

	static final PathProperty<Double> UNIT_PRICE = PathProperty.create("pdv", Double.class)
			.withValidator(Validator.notNegative());

	static final PropertySet<?> TENANT = PropertySet.of(CEP, CIDADE, PDV);

	static final DataTarget<String> TARGET = DataTarget.named("tenant");

}
