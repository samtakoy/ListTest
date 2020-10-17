package ru.samtakoy.listtest.presentation.list.inner

import android.content.Context
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.ConcurrentHashMap

class SwipeItemHelper(
    context: Context,
    private val listener: SwipeListener
    ) : RecyclerView.OnItemTouchListener, RecyclerView.OnChildAttachStateChangeListener{


    interface SwipeListener{
        fun onSwiped(viewHolder: RecyclerView.ViewHolder)
    }

    private val animations = ConcurrentHashMap<RecyclerView.ViewHolder, DynamicAnimation<*>>()
    private val touchSlop:Int = ViewConfiguration.get(context).scaledTouchSlop * 3
    private var initialTouchX: Float = 0f
    private var recyclerView: RecyclerView? = null
    private var velocityTracker: VelocityTracker? = null
    // текущий под пальцем, пока тянем
    private var swipedChild: View? = null


    fun attachToRecyclerView(recyclerView: RecyclerView){
        this.recyclerView = recyclerView
        recyclerView.addOnItemTouchListener(this)
        recyclerView.addOnChildAttachStateChangeListener(this)
    }

    fun detachToRecyclerView(){
        recyclerView.let {

            recyclerView!!.removeOnItemTouchListener(this)
            recyclerView!!.removeOnChildAttachStateChangeListener(this)

            cancelAnimations()
            recyclerView = null
        }

        velocityTracker?.apply {
            recycle()
            velocityTracker = null
        }
    }

    private fun cancelAnimations() {
        for (animation in animations.values) {
            animation.cancel()
        }
        animations.clear()
    }

    private fun animateWithFling(swipeViewHolder: RecyclerView.ViewHolder,velocity: Float){
        val targetView = swipeViewHolder.itemView
        val flingAnimation = FlingAnimation(targetView, DynamicAnimation.TRANSLATION_X)
        flingAnimation.friction = 1f
        flingAnimation.setStartVelocity(velocity)
        flingAnimation.setMaxValue(targetView.width.toFloat())
        swipeViewHolder.setIsRecyclable(false)
        flingAnimation.addEndListener{
                anim, canceled, value, velocity ->

            animations.remove(swipeViewHolder)
            swipeViewHolder.setIsRecyclable(true)

            if(value >= recyclerView!!.width){

                // TODO пока просто возвращаю на место
                targetView.translationX = 0f
                listener.onSwiped(swipeViewHolder)
            } else{
                animateWithSpring(swipeViewHolder, velocity)
            }
        }
        flingAnimation.start()
        animations[swipeViewHolder] = flingAnimation
    }

    private fun animateWithSpring(swipeViewHolder: RecyclerView.ViewHolder, velocity: Float){
        val targetView = swipeViewHolder.itemView
        val springAnimation = SpringAnimation(swipeViewHolder.itemView, DynamicAnimation.TRANSLATION_X)
        springAnimation.setStartVelocity(velocity)
        val springForce = SpringForce(0f)
        springForce.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
        springForce.stiffness = SpringForce.STIFFNESS_LOW
        springAnimation.spring = springForce
        swipeViewHolder.setIsRecyclable(false)
        springAnimation.addEndListener{
            anim, canceled, value, velocity ->
            animations.remove(swipeViewHolder)
            swipeViewHolder.setIsRecyclable(true)
        }
        springAnimation.start()
        animations[swipeViewHolder] = springAnimation
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        when(e.actionMasked){
            MotionEvent.ACTION_DOWN -> {
                initialTouchX = e.x
                velocityTracker?.apply {
                    recycle()
                }
                velocityTracker = VelocityTracker.obtain()
                velocityTracker!!.addMovement(e)
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                if(velocityTracker != null) {
                    velocityTracker!!.addMovement(e)
                    val dX = e.x - initialTouchX
                    val dragged = dX > touchSlop
                    if (dragged) {
                        swipedChild = rv.findChildViewUnder(e.x, e.y)
                    }
                    return dragged
                }
                return false
            }
            else -> return false
        }
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        if(swipedChild == null){
            return
        }

        velocityTracker!!.addMovement(e)

        when(e.actionMasked){
            MotionEvent.ACTION_MOVE -> {
                val dX: Float = e.x - initialTouchX
                swipedChild!!.translationX = dX
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocityTracker!!.computeCurrentVelocity(1000)
                val swipeViewHolder: RecyclerView.ViewHolder = rv.findContainingViewHolder(
                    swipedChild!!
                )
                ?: return

                val velocity: Float = velocityTracker!!.xVelocity
                if(velocity > 0){
                    animateWithFling(swipeViewHolder, velocity)
                } else {
                    animateWithSpring(swipeViewHolder, velocity)
                }
                velocityTracker!!.clear()
            }
        }
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // do nothing
    }

    override fun onChildViewAttachedToWindow(view: View) {
        // do nothing
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        view.translationX = 0f
        val holder: RecyclerView.ViewHolder = recyclerView!!.getChildViewHolder(view)
        if(holder != null){
            animations.remove(holder)
        }
    }
}