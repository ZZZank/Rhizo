package dev.latvian.mods.rhino;

public class IdFunctionObjectES6 extends IdFunctionObject {

	private static final long serialVersionUID = -8023088662589035261L;

	public IdFunctionObjectES6(IdFunctionCall idcall, Object tag, int id, String name, int arity, Scriptable scope) {
		super(idcall, tag, id, name, arity, scope);
	}

	private static final int Id_length = 1, Id_name = 3;
	private boolean myLength = true;
	private boolean myName = true;

	@Override
	protected int findInstanceIdInfo(Context cx, String s) {
		if (s.equals("length")) {
			return instanceIdInfo(READONLY | DONTENUM, Id_length);
		} else if (s.equals("name")) {
			return instanceIdInfo(READONLY | DONTENUM, Id_name);
		}
		return super.findInstanceIdInfo(cx, s);
	}

	@Override
	protected Object getInstanceIdValue(Context cx, int id) {
		if (id == Id_length && !myLength) {
			return NOT_FOUND;
		} else if (id == Id_name && !myName) {
			return NOT_FOUND;
		}
		return super.getInstanceIdValue(cx, id);
	}

	@Override
	protected void setInstanceIdValue(Context cx, int id, Object value) {
		if (id == Id_length && value == NOT_FOUND) {
			this.myLength = false;
			return;
		} else if (id == Id_name && value == NOT_FOUND) {
			this.myName = false;
			return;
		}
		super.setInstanceIdValue(cx, id, value);
	}
}
