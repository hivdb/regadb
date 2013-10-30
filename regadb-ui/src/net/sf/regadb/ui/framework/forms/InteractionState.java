package net.sf.regadb.ui.framework.forms;

public enum InteractionState
{
	Adding {
        @Override
        public boolean isEditable() {
            return true;
        }
    },
	Editing {
        @Override
        public boolean isEditable() {
            return true;
        }
    },
	Viewing {
        @Override
        public boolean isEditable() {
            return false;
        }
    },
    Deleting {
        @Override
        public boolean isEditable() {
            return false;
        }
    };
    
    public abstract boolean isEditable();
}
