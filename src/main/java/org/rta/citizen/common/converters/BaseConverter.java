package org.rta.citizen.common.converters;

import java.util.Collection;

public interface BaseConverter<S,T> {
	
	public abstract T convertToModel(S source);
	public abstract S convertToEntity(T source);
	public abstract Collection<T> convertToModelList(Collection<S> source);
	public abstract Collection<S> convertToEntityList(Collection<T> source);

}
