package AIProjectCode;

import ProjectThreeEngine.DirType;
import ProjectThreeEngine.GameState;

public class MemoryState 
{
    GameState mState;
    DirType mLastMove;
    int mReward;
    GameState mNextState;
    boolean mIsOver;
    public MemoryState(GameState state, DirType lastMove, int reward, GameState nextState, boolean isOver)
    {
        mState = state;
        mLastMove = lastMove;
        mReward = reward;
        mNextState = nextState;
        mIsOver = isOver;
    }

    public GameState getState()
    {
        return mState;
    }

    public DirType getLastMove()
    {
        return mLastMove;
    }

    public int getReward()
    {
        return mReward;
    }

    public GameState getNextState()
    {
        return mNextState;
    }

    public boolean getIsOver()
    {
        return mIsOver;
    }
    
}
