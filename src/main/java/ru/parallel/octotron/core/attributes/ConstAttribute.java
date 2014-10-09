package ru.parallel.octotron.core.attributes;

import ru.parallel.octotron.core.collections.AttributeList;
import ru.parallel.octotron.core.logic.Reaction;
import ru.parallel.octotron.core.model.IModelAttribute;
import ru.parallel.octotron.core.model.ModelEntity;
import ru.parallel.octotron.core.primitive.EAttributeType;
import ru.parallel.octotron.core.primitive.exception.ExceptionModelFail;
import ru.parallel.octotron.reactions.PreparedResponse;

import java.util.Collection;
import java.util.LinkedList;

public class ConstAttribute extends AbstractAttribute implements IModelAttribute
{
	private static final String err_msg = "unsupported operation on const attribute: ";

	public static class ConstAttributeBuilder implements IAttributeBuilder
	{
		@Override
		public void AddReaction(Reaction reaction)
		{
			throw new ExceptionModelFail(err_msg + "AddReaction");
		}

		@Override
		public void AddDependant(VarAttribute attribute)
		{
			// nothing to see here
			// throw new ExceptionModelFail(err_msg + "AddDependant");
		}
	}

	public ConstAttribute(ModelEntity parent, String name, Object value)
	{
		super(EAttributeType.CONST, parent, name, value);

		parent.StorePersistentAttribute(name, value);
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
	public Collection<PreparedResponse> ProcessReactions()
	{
		return new LinkedList<>();
	}

	@Override
	public IAttributeBuilder GetBuilder()
	{
		return new ConstAttributeBuilder();
	}
}
