package share.fare.backend.service.strategy;

import share.fare.backend.entity.SplitType;

public class SplitStrategyFactory {
    public static SplitStrategy getStrategy(SplitType splitType) {
        if (splitType == null) {
            throw new IllegalArgumentException("Split type cannot be null");
        }

        return switch (splitType) {
            case EQUALLY -> new EqualSplitStrategy();
            case PERCENTAGE -> new PercentageSplitStrategy();
            case AMOUNT -> new AmountSplitStrategy();
            case SHARES -> new SharesSplitStrategy();
        };
    }
}

