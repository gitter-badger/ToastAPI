package jaci.openrio.toast.core.script.js;

import jaci.openrio.toast.core.StateTracker;
import jaci.openrio.toast.core.thread.Heartbeat;
import jaci.openrio.toast.core.thread.HeartbeatListener;
import jaci.openrio.toast.lib.state.RobotState;
import jaci.openrio.toast.lib.state.StateListener;

import javax.script.ScriptException;

/**
 * The JSEngine is responsible for managing the interaction between Toast <--> JavaScript. This includes things such as
 * State Operations and Heartbeats. This is used to avoid the overhead of having every state operation function in JavaScript
 * registered through Nashorn, but instead have a single call that is then iterated in the Javascript engine.
 *
 * @author Jaci
 */
public class JSEngine implements StateListener.Transition, StateListener.Ticker, HeartbeatListener {

    static JSEngine engine;
    static boolean hbeat;

    /**
     * Initialize the JavaScript engine (assuming it's supported)
     */
    protected static void init() {
        if (JavaScript.supported()) {
            engine = new JSEngine();
            StateTracker.addTransition(engine);
            StateTracker.addTicker(engine);
        }
    }

    /**
     * Call the JavaScript functions related to ticking (auto(), teleop(), disabled() etc) when the
     * Robot Program is ticking in a state.
     */
    @Override
    public void tickState(RobotState state) {
        try {
            JavaScript.eval("t_$.tick('" + state.state.toLowerCase() + "')");
        } catch (ScriptException e) { }
    }

    /**
     * Call the JavaScript functions related to transitioning (onauto(), onteleop(), ondisabled() etc) when the
     * Robot Program undergoes a state transition.
     */
    @Override
    public void transitionState(RobotState state, RobotState oldState) {
        try {
            JavaScript.eval("t_$.trans('" + state.state.toLowerCase() + "')");
        } catch (ScriptException e) { }
    }

    /**
     * Called by JavaScript when a Heartbeat listener is added, to make sure the Heartbeat doesn't start
     * until it is required.
     */
    public static void hb() {
        if (!hbeat) {
            hbeat = true;
            Heartbeat.add(engine);
        }
    }

    /**
     * Call the JavaScript heartbeat() callbacks when a Heartbeat occurs.
     */
    @Override
    public void onHeartbeat(int skipped) {
        try {
            JavaScript.eval("t_$.heartbeat(" + skipped + ")");
        } catch (ScriptException e) { }
    }
}
