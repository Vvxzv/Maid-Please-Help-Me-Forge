package net.vvxzv.maidforge.utils;

import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import org.jetbrains.annotations.Nullable;

public class ForgeUtil {
    public enum AdjustedForgeRule {
        HIT_ANY("any", ForgeStep.HIT_LIGHT),
        HIT_NOT_LAST("not_last", ForgeStep.HIT_LIGHT),
        HIT_LAST("last", ForgeStep.HIT_LIGHT),
        HIT_SECOND_LAST("second_last", ForgeStep.HIT_LIGHT),
        HIT_THIRD_LAST("third_last", ForgeStep.HIT_LIGHT),
        DRAW_ANY("any", ForgeStep.DRAW),
        DRAW_LAST("last", ForgeStep.DRAW),
        DRAW_NOT_LAST("not_last", ForgeStep.DRAW),
        DRAW_SECOND_LAST("second_last", ForgeStep.DRAW),
        DRAW_THIRD_LAST("third_last", ForgeStep.DRAW),
        PUNCH_ANY("any", ForgeStep.PUNCH),
        PUNCH_LAST("last", ForgeStep.PUNCH),
        PUNCH_NOT_LAST("not_last", ForgeStep.PUNCH),
        PUNCH_SECOND_LAST("second_last", ForgeStep.PUNCH),
        PUNCH_THIRD_LAST("third_last", ForgeStep.PUNCH),
        BEND_ANY("any", ForgeStep.BEND),
        BEND_LAST("last", ForgeStep.BEND),
        BEND_NOT_LAST("not_last", ForgeStep.BEND),
        BEND_SECOND_LAST("second_last", ForgeStep.BEND),
        BEND_THIRD_LAST("third_last", ForgeStep.BEND),
        UPSET_ANY("any", ForgeStep.UPSET),
        UPSET_LAST("last", ForgeStep.UPSET),
        UPSET_NOT_LAST("not_last", ForgeStep.UPSET),
        UPSET_SECOND_LAST("second_last", ForgeStep.UPSET),
        UPSET_THIRD_LAST("third_last", ForgeStep.UPSET),
        SHRINK_ANY("any", ForgeStep.SHRINK),
        SHRINK_LAST("last", ForgeStep.SHRINK),
        SHRINK_NOT_LAST("not_last", ForgeStep.SHRINK),
        SHRINK_SECOND_LAST("second_last", ForgeStep.SHRINK),
        SHRINK_THIRD_LAST("third_last", ForgeStep.SHRINK);

        private final String type;
        private final ForgeStep step;
        private static final AdjustedForgeRule[] VALUES = values();

        public static @Nullable AdjustedForgeRule valueOf(int id) {
            return id >= 0 && id < VALUES.length ? VALUES[id] : null;
        }

        AdjustedForgeRule(String lastForgeType, ForgeStep step){
            this.type = lastForgeType;
            this.step = step;
        }

        public static ForgeStep[] autoLastSteps(ForgeRule[] rules){
            ForgeStep[] lastSteps = new ForgeStep[]{null, null, null};
            for(ForgeRule rule : rules) {
                AdjustedForgeRule adjustedRule = AdjustedForgeRule.valueOf(rule.ordinal());
                switch (adjustedRule.type) {
                    case "third_last":
                        lastSteps[2] = adjustedRule.step;
                        break;
                    case "second_last":
                        lastSteps[1] = adjustedRule.step;
                        break;
                    case "last":
                        lastSteps[0] = adjustedRule.step;
                        break;
                    case "any", "not_last":
                        fillEmptyLastSteps(rules, lastSteps, adjustedRule.step);
                        break;
                }
            }
            return lastSteps;
        }

        private static void fillEmptyLastSteps(ForgeRule[] rules, ForgeStep[] lastSteps, ForgeStep step) {
            if (lastSteps[2] == null && rules.length > 2) {
                lastSteps[2] = step;
            } else if (lastSteps[1] == null && rules.length > 1) {
                lastSteps[1] = step;
            } else if (lastSteps[0] == null) {
                lastSteps[0] = step;
            }
        }
    }
}
