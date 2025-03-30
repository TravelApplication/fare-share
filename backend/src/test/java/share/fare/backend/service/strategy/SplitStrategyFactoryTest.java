package share.fare.backend.service.strategy;

import org.junit.jupiter.api.Test;
import share.fare.backend.entity.SplitType;

import static org.junit.jupiter.api.Assertions.*;

class SplitStrategyFactoryTest {

    @Test
    void testGetStrategyEqual() {
        SplitStrategy strategy = SplitStrategyFactory.getStrategy(SplitType.EQUALLY);
        assertNotNull(strategy);
        assertInstanceOf(EqualSplitStrategy.class, strategy);
    }

    @Test
    void testGetStrategyPercentage() {
        SplitStrategy strategy = SplitStrategyFactory.getStrategy(SplitType.PERCENTAGE);
        assertNotNull(strategy);
        assertInstanceOf(PercentageSplitStrategy.class, strategy);
    }

    @Test
    void testGetStrategyAmount() {
        SplitStrategy strategy = SplitStrategyFactory.getStrategy(SplitType.AMOUNT);
        assertNotNull(strategy);
        assertInstanceOf(AmountSplitStrategy.class, strategy);
    }

    @Test
    void testGetStrategyShares() {
        SplitStrategy strategy = SplitStrategyFactory.getStrategy(SplitType.SHARES);
        assertNotNull(strategy);
        assertInstanceOf(SharesSplitStrategy.class, strategy);
    }

    @Test
    void testGetStrategyInvalidTypeThrowsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            SplitStrategyFactory.getStrategy(null);
        });

        assertEquals("Split type cannot be null", exception.getMessage());
    }

    @Test
    void testFactoryInstantiation() {
        SplitStrategyFactory factory = new SplitStrategyFactory();
        assertNotNull(factory);
        assertInstanceOf(SplitStrategyFactory.class, factory);
    }

}