package net.vvxzv.maidforge.utils;

import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;

import java.util.HashMap;
import java.util.Map;

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

        public static AdjustedForgeRule valueOf(int id) {
            return VALUES[id];
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
                    case "any":
                        anyLastSteps(lastSteps, adjustedRule.step);
                        break;
                    case "not_last":
                        notLastLastSteps(rules, lastSteps, adjustedRule.step);
                        break;
                }
            }
            return lastSteps;
        }

        private static void notLastLastSteps(ForgeRule[] rules, ForgeStep[] lastSteps, ForgeStep step) {
            if (lastSteps[2] == null && rules.length > 2) {
                lastSteps[2] = step;
            }
            else if (lastSteps[1] == null && rules.length > 1) {
                lastSteps[1] = step;
            }
        }

        private static void anyLastSteps(ForgeStep[] lastSteps, ForgeStep step) {
            if (lastSteps[0] == null) {
                lastSteps[0] = step;
            } else if (lastSteps[1] == null) {
                lastSteps[1] = step;
            } else if (lastSteps[2] == null) {
                lastSteps[2] = step;
            }
        }
    }

    private static final Map<Integer, ForgeStep> DELTA_TO_FORGE_STEP = new HashMap<>();
    private static final Map<Integer, ForgeStep> DELTA_TO_FORGE_STEP_SUPPLEMENT = new HashMap<>();
    static {
        DELTA_TO_FORGE_STEP.put(40, ForgeStep.UPSET);       // 13 + 13 + 7 + 7; 13 + [27]
        DELTA_TO_FORGE_STEP.put(35, ForgeStep.UPSET);       // 13 + 13 + 7 + 2; 13 + [22]
        DELTA_TO_FORGE_STEP.put(33, ForgeStep.UPSET);       // 13 + 13 + 7; 13 + [20]
        DELTA_TO_FORGE_STEP.put(28, ForgeStep.UPSET);       // 13 + 13 + 2; 13 + [15]
        DELTA_TO_FORGE_STEP.put(27, ForgeStep.UPSET);       // 13 + 7 + 7; 13 + [14]
        DELTA_TO_FORGE_STEP.put(26, ForgeStep.UPSET);       // 13 + 13; 13 + [13]
        DELTA_TO_FORGE_STEP.put(22, ForgeStep.UPSET);       // 13 + 7 + 2; 13 + [9]
        DELTA_TO_FORGE_STEP.put(21, ForgeStep.BEND);        // 7 + 7 + 7; 7 + [14]
        DELTA_TO_FORGE_STEP.put(20, ForgeStep.UPSET);       // 13 + 7; 13 + [7]
        DELTA_TO_FORGE_STEP.put(15, ForgeStep.UPSET);       // 13 + 2; 13 + [2]
        DELTA_TO_FORGE_STEP.put(14, ForgeStep.BEND);        // 7 + 7; 7 + [7]
        DELTA_TO_FORGE_STEP.put(13, ForgeStep.UPSET);
        DELTA_TO_FORGE_STEP.put(12, ForgeStep.HIT_LIGHT);   // -3 + 13 + 2; -3 + [15]
        DELTA_TO_FORGE_STEP.put(11, ForgeStep.BEND);        // 7 + 2 + 2; 7 + [4]
        DELTA_TO_FORGE_STEP.put(10, ForgeStep.HIT_LIGHT);   // -3 + 13; -3 + [13]
        DELTA_TO_FORGE_STEP.put(9, ForgeStep.BEND);         // 7 + 2; 7 + [2]
        DELTA_TO_FORGE_STEP.put(8, ForgeStep.HIT_MEDIUM);   // -6 + 7 + 7; -6 + [7]
        DELTA_TO_FORGE_STEP.put(7, ForgeStep.BEND);
        DELTA_TO_FORGE_STEP.put(6, ForgeStep.PUNCH);        // 2 + 2 + 2; 2 + [4]
        DELTA_TO_FORGE_STEP.put(5, ForgeStep.HIT_MEDIUM);   // -6 + 7 + 2 + 2; -6 + [11]
        DELTA_TO_FORGE_STEP.put(4, ForgeStep.PUNCH);        // 2 + 2; 2 + [2]
        DELTA_TO_FORGE_STEP.put(3, ForgeStep.HIT_MEDIUM);   // -6 + 7 + 2; -6 + [9]
        DELTA_TO_FORGE_STEP.put(2, ForgeStep.PUNCH);
        DELTA_TO_FORGE_STEP.put(1, ForgeStep.HIT_MEDIUM);   // -6 + 7; -6 + [7]
        DELTA_TO_FORGE_STEP.put(-3, ForgeStep.HIT_LIGHT);
        DELTA_TO_FORGE_STEP.put(-6, ForgeStep.HIT_MEDIUM);
        DELTA_TO_FORGE_STEP.put(-9, ForgeStep.HIT_HARD);

        // 补充部分极端情况
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(12, ForgeStep.PUNCH);        // 2 + 13 -3; 2 + [10]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(11, ForgeStep.BEND);         // 7 + 2 + 2; 7 + [4]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(10, ForgeStep.UPSET);        // 13 -3; 13 + [-3]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(9, ForgeStep.BEND);          // 7 + 2; 7 + [2]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(8, ForgeStep.BEND);          // 7 + 7 -6; 7 + [1]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(7, ForgeStep.BEND);
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(6, ForgeStep.PUNCH);         // 2 + 2 + 2; 2 + [4]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(5, ForgeStep.BEND);          // 7 + 7 -9; 7 + [-2]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(4, ForgeStep.PUNCH);         // 2 + 2; 2 + [2]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(3, ForgeStep.BEND);          // 7 + 2 -6; 7 + [-4]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(2, ForgeStep.PUNCH);
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(1, ForgeStep.BEND);          // 7 -6; 7 + [-6]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(-2, ForgeStep.BEND);         // 7 -9; 2 + [-9]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(-3, ForgeStep.HIT_LIGHT);
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(-4, ForgeStep.PUNCH);        // 2 -6; 2 + [-6]
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(-6, ForgeStep.HIT_MEDIUM);
        DELTA_TO_FORGE_STEP_SUPPLEMENT.put(-9, ForgeStep.HIT_HARD);
    }

    public static ForgeStep findForgeStep(int currentWork, int delta) {
        ForgeStep forgeStep = DELTA_TO_FORGE_STEP.get(delta);
        if(currentWork + delta < 13) {
            return DELTA_TO_FORGE_STEP_SUPPLEMENT.get(delta);
        }
        if(forgeStep == null){
            if (delta < 0) {
                forgeStep = ForgeStep.DRAW;
            }
            else if (delta >= 16) {
                forgeStep = ForgeStep.SHRINK;
            }
        }
        return forgeStep;
    }
}
