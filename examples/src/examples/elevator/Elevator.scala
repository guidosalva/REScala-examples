package examples.elevator
import rescala.SignalSynt
import rescala.events._
import rescala.Var
import rescala.Signal
import makro.SignalMacro.{SignalM => Signal}
import scala.swing._
import examples.datastructures._

class Elevator(val nFloors : Int) { 
	// some constants
	val FloorHeight = 120
	val MaxSpeed = 10
	val MaxAccel = 1
	val BreakTime =  MaxSpeed / MaxAccel // number of ticks it takes to break down
	val BreakDist = (1 to BreakTime)./:(0) {_ + MaxSpeed - MaxAccel * _}
	val FloorStart = 10
	val FloorPos = Iterable.iterate(FloorStart, nFloors)(_ + FloorHeight).toList
	val WaitingTime = 10 // number of ticks to wait on each floor

	// expose this event to the outside
	val tick = new ImperativeEvent[Unit]	
	val callToFloor = new ImperativeEvent[Int]
		
	val queue = SQueue(1)
	
	// helper function integrating values over tick (time)
	def integrate(f : => Int) : Signal[Int] = tick.iterate(0) {_ + f /* *delta_t */}
	
	// Define Signals describing state and behavior of the elevator
	val destination = Signal {
	  queue.head() match {
	    case None => position()
	    case Some(target) => FloorPos(target)
	  }
	}
	
	val position : Signal[Int] = integrate { speed() * direction() }
	val speed  = tick.iterate(0) { v =>  math.min(v + accelaration(), MaxSpeed)}
	val stopped = Signal { speed() == 0 }	
	val distance = Signal { destination() - position() }
	val direction = Signal { math.signum(distance()) }
	val accelaration : Signal[Int] = Signal {
	  val break = math.abs(distance()) <= BreakDist
	  if(break){
	    if(stopped()) 0 
	    else -MaxAccel
	  }
	  else MaxAccel
	}
	val currentFloor = Signal { 
	  val p = position()
	  FloorPos.indexOf(FloorPos.sortBy(f => math.abs(f - p)).head)
	}	
	val reached = Signal { stopped() && position() == destination() }
	val reachedFloor : Event[Int] = reached.changed && {_ == true} map 
		{(_ : Boolean) => currentFloor()}
	
	val waitingTime = reachedFloor.reset(0) {(floor : Int) => 
	    tick.iterate(WaitingTime){_ - (if(isWaiting()) 1 else 0) }
	}
	val stoppedWaiting = waitingTime.changedTo(0)
	val isWaiting : Signal[Boolean] = 
	  (reachedFloor.map((_ : Int) => true) || stoppedWaiting.map((_ : Any) => false)).latest(false)
	
	 
	// Define some behavior with events
	stoppedWaiting += {_ => queue.dequeue} // move to the next destination	
	callToFloor += {queue enqueue _} // enqueue a new floor
	
	// debugging
	/*
	tick += {_ =>
	  println(position() + "; speed = " + speed() + "; waiting = " + waitingTime() + " " + isWaiting())
	}	
	stoppedWaiting += {x => println("!Stopped waiting")}
	reachedFloor += {x => println("!Reached floor " + x)}
	*/
	
	def nameOfFloor(i : Int) = (nFloors - i) match {
	    case 1 => "E"
	    case n => "" + n
	}
}

object Test extends App {
  val e = new Elevator(3)
  e callToFloor 2
  e callToFloor 2
  
  for(_ <- 0 to 100) e.tick()
}


