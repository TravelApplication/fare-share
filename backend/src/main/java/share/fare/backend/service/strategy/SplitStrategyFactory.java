package share.fare.backend.service.strategy;

import share.fare.backend.entity.SplitType;

public class SplitStrategyFactory {
    public static SplitStrategy getStrategy(SplitType splitType) {
        switch (splitType) {
            case EQUALLY:
                return new EqualSplitStrategy();
            case PERCENTAGE:
                return new PercentageSplitStrategy();
            case AMOUNT:
                return new AmountSplitStrategy();
            case SHARES:
                return new SharesSplitStrategy();
            default:
                throw new IllegalArgumentException("Unknown split type: " + splitType);
        }
    }
}

