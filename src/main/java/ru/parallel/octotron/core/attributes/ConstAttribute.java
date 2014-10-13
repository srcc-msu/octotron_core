package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.logic.Response;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.model.ModelService;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;

import java.util.Collection;
import java.util.LinkedList;

public class ConstAttribute extends AbstractAttribute implements IModelAttribute
{
	static final String err_msg = "unsupported operation on const attribute: ";

	@Override
	public IAttributeBuilder GetBuilder(ModelService service)
	{
		service.CheckModification();

		return new ConstAttributeBuilder(service);
	}

	public ConstAttribute(ModelEntity parent, String name, Object value)
	{
		super(EAttributeType.CONST, parent, name, value);
	}

	@Override
	public EAttributeType GetType()
	{
		return EAttributeType.CONST;
	}

	@Override
	public Reaction GetReaction(long id)
	{
		throw new ExceptionModelFail(err_msg + "GetReaction");
	}

	@Override
	public boolean IsValid()
	{
		return true;
	}

	@Override
	public void SetValid() { throw new ExceptionModelFail(err_msg + "SetValid"); }
	@Override
	public void SetInvalid() { throw new ExceptionModelFail(err_msg + "SetInvalid"); }

	@Override
	public AttributeList<VarAttribute> GetDependant()
	{
		return new AttributeList<>();
	}

	@Override
	public double GetSpeed()
	{
		return 0.0;
	}

	@Override
	public Collection<Reaction> GetReactions()
	{
		return new LinkedList<>();
	}

	@Override
	public Collection<Response> ProcessReactions()
	{
		return new LinkedList<>();
	}
}