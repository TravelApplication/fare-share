package share.fare.backend.service.strategy;

import share.fare.backend.entity.SplitType;

public class SplitStrategyFactory {
    public static SplitStrategy getStrategy(SplitType splitType) {
        return switch (splitType) {
            case EQUALLY -> new EqualSplitStrategy();
            case PERCENTAGE -> new PercentageSplitStrategy();
            case AMOUNT -> new AmountSplitStrategy();
            case SHARES -> new SharesSplitStrategy();
            default -> throw new IllegalArgumentException("Unknown split type: " + splitType);
        };
    }
}

