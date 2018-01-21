package com.android.systemui.recents.tv.views;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.events.EventBus;
import com.android.systemui.recents.events.activity.LaunchTvTaskEvent;
import com.android.systemui.recents.events.ui.DeleteTaskDataEvent;
import com.android.systemui.recents.model.RecentsTaskLoader;
import com.android.systemui.recents.model.Task;
import com.android.systemui.recents.model.TaskStack;
import com.android.systemui.recents.views.AnimationProps;
import java.util.ArrayList;
import java.util.List;

public class TaskStackHorizontalViewAdapter
  extends RecyclerView.Adapter<ViewHolder>
{
  private TaskStackHorizontalGridView mGridView;
  private List<Task> mTaskList;
  
  public TaskStackHorizontalViewAdapter(List paramList)
  {
    this.mTaskList = new ArrayList(paramList);
  }
  
  public void addTaskAt(Task paramTask, int paramInt)
  {
    this.mTaskList.add(paramInt, paramTask);
    notifyItemInserted(paramInt);
  }
  
  public int getItemCount()
  {
    return this.mTaskList.size();
  }
  
  public int getPositionOfTask(Task paramTask)
  {
    int i = this.mTaskList.indexOf(paramTask);
    if (i >= 0) {
      return i;
    }
    return 0;
  }
  
  public void onBindViewHolder(ViewHolder paramViewHolder, int paramInt)
  {
    Task localTask = (Task)this.mTaskList.get(paramInt);
    Recents.getTaskLoader().loadTaskData(localTask);
    paramViewHolder.init(localTask);
  }
  
  public ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new ViewHolder(LayoutInflater.from(paramViewGroup.getContext()).inflate(2130968800, paramViewGroup, false));
  }
  
  public void removeTask(Task paramTask)
  {
    int i = this.mTaskList.indexOf(paramTask);
    if (i >= 0)
    {
      this.mTaskList.remove(i);
      notifyItemRemoved(i);
      if (this.mGridView != null) {
        this.mGridView.getStack().removeTask(paramTask, AnimationProps.IMMEDIATE, false);
      }
    }
  }
  
  public void setNewStackTasks(List paramList)
  {
    this.mTaskList.clear();
    this.mTaskList.addAll(paramList);
    notifyDataSetChanged();
  }
  
  public void setTaskStackHorizontalGridView(TaskStackHorizontalGridView paramTaskStackHorizontalGridView)
  {
    this.mGridView = paramTaskStackHorizontalGridView;
  }
  
  public class ViewHolder
    extends RecyclerView.ViewHolder
    implements View.OnClickListener
  {
    private Task mTask;
    private TaskCardView mTaskCardView;
    
    public ViewHolder(View paramView)
    {
      super();
      this.mTaskCardView = ((TaskCardView)paramView);
    }
    
    private Animator.AnimatorListener getRemoveAtListener(int paramInt, final Task paramTask)
    {
      new Animator.AnimatorListener()
      {
        public void onAnimationCancel(Animator paramAnonymousAnimator) {}
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if ((paramTask == null) || (paramTask.isLocked)) {
            return;
          }
          TaskStackHorizontalViewAdapter.this.removeTask(paramTask);
          EventBus.getDefault().send(new DeleteTaskDataEvent(paramTask));
        }
        
        public void onAnimationRepeat(Animator paramAnonymousAnimator) {}
        
        public void onAnimationStart(Animator paramAnonymousAnimator) {}
      };
    }
    
    public void init(Task paramTask)
    {
      this.mTaskCardView.init(paramTask);
      this.mTask = paramTask;
      this.mTaskCardView.setOnClickListener(this);
    }
    
    public void onClick(View paramView)
    {
      try
      {
        if (this.mTaskCardView.isInDismissState())
        {
          this.mTaskCardView.startDismissTaskAnimation(getRemoveAtListener(getAdapterPosition(), this.mTaskCardView.getTask()));
          return;
        }
        EventBus.getDefault().send(new LaunchTvTaskEvent(this.mTaskCardView, this.mTask, null, -1));
        return;
      }
      catch (Exception localException)
      {
        Log.e("TaskStackViewAdapter", paramView.getContext().getString(2131690329, new Object[] { this.mTask.title }), localException);
      }
    }
  }
}


/* Location:              C:\Users\johan\Desktop\classes-dex2jar.jar!\com\android\systemui\recents\tv\views\TaskStackHorizontalViewAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */