package com.artisztikum.ac.cache;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for unmarshaling {@link Source}s into <code>E</code>.
 * 
 * @author Adam DAJKA (dajka@artisztikum.hu)
 * 
 * @param <E>
 *            The type to unmarsal into.
 */
public abstract class AbstractUnmarshaller<E>
{
	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AbstractUnmarshaller.class);

	/**
	 * The {@link JAXBContext} to use during the unmarshalling.
	 */
	private final JAXBContext jcContext;

	/**
	 * Default constructor. Initializes {@link #jcContext} with calling the abstract method
	 * {@link #getMarshallerContext()}.
	 */
	public AbstractUnmarshaller()
	{
		jcContext = getMarshallerContext();
	}

	/**
	 * Subclasses must implement this, but never call directly. The returned {@link JAXBContext} will be used in
	 * {@link #unmarshal(Source)}.
	 * 
	 * @return The {@link JAXBContext} to use during the unmarshalling.
	 */
	protected abstract JAXBContext getMarshallerContext();

	/**
	 * Subclasses must implement this, but never call directly. The returned {@link Class} will be used in
	 * {@link #unmarshal(Source)}.
	 * 
	 * @return The target class.
	 */
	protected abstract Class<E> getTargetClass();

	/**
	 * @param src
	 *            The source to unmarshal {@code E} from.
	 * @return An instance of target type {@code E}.
	 */
	public final E unmarshal(final Source src)
	{
		try {
			final Unmarshaller u = jcContext.createUnmarshaller();
			u.setEventHandler(new ValidationEventHandler() {
				public boolean handleEvent(final ValidationEvent event)
				{
					if (event.getMessage().contains("unexpected element")) {
						return true;
					}
					LOG.warn(event.getMessage(), event.getLinkedException());
					return true;
				}
			});
			final JAXBElement<E> doc = u.unmarshal(src, getTargetClass());
			return doc.getValue();
		} catch (final JAXBException e) {
			throw new RuntimeException("Error de-serializing element", e);
		}
	}
}
