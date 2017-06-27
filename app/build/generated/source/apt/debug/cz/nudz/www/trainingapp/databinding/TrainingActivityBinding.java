package cz.nudz.www.trainingapp.databinding;
import cz.nudz.www.trainingapp.R;
import cz.nudz.www.trainingapp.BR;
import android.view.View;
public class TrainingActivityBinding extends android.databinding.ViewDataBinding  {

    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.trainingActivityGuideCenter, 1);
        sViewsWithIds.put(R.id.trainingActivityGuideLeft, 2);
        sViewsWithIds.put(R.id.trainingActivityGuideRight, 3);
        sViewsWithIds.put(R.id.trainingActivityGuideBottom, 4);
        sViewsWithIds.put(R.id.trainingActivityFixationPoint, 5);
    }
    // views
    public final android.widget.ImageView trainingActivityFixationPoint;
    public final android.support.constraint.Guideline trainingActivityGuideBottom;
    public final android.support.constraint.Guideline trainingActivityGuideCenter;
    public final android.support.constraint.Guideline trainingActivityGuideLeft;
    public final android.support.constraint.Guideline trainingActivityGuideRight;
    public final android.support.constraint.ConstraintLayout trainingActivityRootLayout;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public TrainingActivityBinding(android.databinding.DataBindingComponent bindingComponent, View root) {
        super(bindingComponent, root, 0);
        final Object[] bindings = mapBindings(bindingComponent, root, 6, sIncludes, sViewsWithIds);
        this.trainingActivityFixationPoint = (android.widget.ImageView) bindings[5];
        this.trainingActivityGuideBottom = (android.support.constraint.Guideline) bindings[4];
        this.trainingActivityGuideCenter = (android.support.constraint.Guideline) bindings[1];
        this.trainingActivityGuideLeft = (android.support.constraint.Guideline) bindings[2];
        this.trainingActivityGuideRight = (android.support.constraint.Guideline) bindings[3];
        this.trainingActivityRootLayout = (android.support.constraint.ConstraintLayout) bindings[0];
        this.trainingActivityRootLayout.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean setVariable(int variableId, Object variable) {
        switch(variableId) {
        }
        return false;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;

    public static TrainingActivityBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot) {
        return inflate(inflater, root, attachToRoot, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static TrainingActivityBinding inflate(android.view.LayoutInflater inflater, android.view.ViewGroup root, boolean attachToRoot, android.databinding.DataBindingComponent bindingComponent) {
        return android.databinding.DataBindingUtil.<TrainingActivityBinding>inflate(inflater, cz.nudz.www.trainingapp.R.layout.training_activity, root, attachToRoot, bindingComponent);
    }
    public static TrainingActivityBinding inflate(android.view.LayoutInflater inflater) {
        return inflate(inflater, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static TrainingActivityBinding inflate(android.view.LayoutInflater inflater, android.databinding.DataBindingComponent bindingComponent) {
        return bind(inflater.inflate(cz.nudz.www.trainingapp.R.layout.training_activity, null, false), bindingComponent);
    }
    public static TrainingActivityBinding bind(android.view.View view) {
        return bind(view, android.databinding.DataBindingUtil.getDefaultComponent());
    }
    public static TrainingActivityBinding bind(android.view.View view, android.databinding.DataBindingComponent bindingComponent) {
        if (!"layout/training_activity_0".equals(view.getTag())) {
            throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
        }
        return new TrainingActivityBinding(bindingComponent, view);
    }
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}