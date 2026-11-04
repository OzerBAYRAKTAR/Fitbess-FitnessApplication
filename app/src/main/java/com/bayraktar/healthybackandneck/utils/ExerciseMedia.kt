package com.bayraktar.healthybackandneck.utils

import androidx.annotation.DrawableRes
import com.bayraktar.healthybackandneck.R
import com.bayraktar.healthybackandneck.data.Models.ExerciseDetailModel.ExerciseDayExercise

/**
 * Stable catalog row IDs survive APK resource-table renumbering; stored image Ints do not.
 * Both JSON catalogs use unique row IDs (exerciseId is NOT unique across their mappings).
 * ExerciseMediaTest verifies every mapping against the source catalogs.
 */
object ExerciseMedia {
    @DrawableRes fun drawable(move: ExerciseDayExercise): Int = drawable(move.id)

    @DrawableRes fun drawable(catalogId: Int): Int = when (catalogId) {
        8, 288, 432, 526 -> R.drawable.e1_b1_sitting_side_bends
        480, 496, 540 -> R.drawable.e10_b10_shoulder_shrug
        50, 177, 218, 250, 378 -> R.drawable.e100_b11_stndng_bndover
        605, 620 -> R.drawable.e100_b11_stndng_bndover
        89, 122, 321, 337, 370, 386, 481, 497, 606 -> R.drawable.e101_b12_chair_squat
        18, 193, 226, 281, 322, 330, 457, 607 -> R.drawable.e102_b13_crab_walk
        306, 498, 522, 532, 608, 621 -> R.drawable.e103_b14_donkey_kick
        97, 202, 233, 489, 609, 623 -> R.drawable.e104_b15_frog_jumps
        153, 217, 273, 305, 410, 531, 610 -> R.drawable.e105_b16_grace_marching_bridge_extension
        57, 98, 257, 314, 329, 353, 466, 611 -> R.drawable.e106_b17_high_donkey_kick
        26, 130, 169, 209, 225, 449, 612 -> R.drawable.e107_b18_squats
        73, 146, 185, 194, 201, 210, 298, 354, 613 -> R.drawable.e108_b19_pull_single_leg
        114, 234, 265, 538, 614 -> R.drawable.e109_b20_single_leg_deadlift
        16, 232, 344, 541 -> R.drawable.e11_b11_get_up_straight
        33, 113, 137, 186, 258, 266, 393, 418, 425, 433, 441, 615 -> R.drawable.e110_b21_step_out_squat_with_pulse
        80, 384, 408, 542 -> R.drawable.e12_b12_sitting_dive
        224, 543 -> R.drawable.e13_b13_lying_dive
        544 -> R.drawable.e14_b14_sitting_shoulder_rotation
        112, 368, 545 -> R.drawable.e15_b15_chair_back_strech
        56, 120, 416, 546 -> R.drawable.e16_b16_superman
        72, 264, 547 -> R.drawable.e17_b17_lying_neck_extension
        61, 133, 198, 205, 317, 350, 575 -> R.drawable.e18_k1_sit_up
        46, 150, 206, 230, 270, 326, 462, 576 -> R.drawable.e19_k2_scissor_kicks
        160, 488, 527, 551 -> R.drawable.e2_b2_neck_rotation
        14, 62, 94, 125, 278, 577 -> R.drawable.e20_k3_bicycle_crunch_slowed
        38, 126, 134, 141, 149, 157, 357, 422, 445, 578 -> R.drawable.e21_k4_russian_twist
        486, 579 -> R.drawable.e22_k5_hold_leg_raise
        37, 110, 117, 238, 285, 318, 493, 580 -> R.drawable.e23_k6_lying_reaches
        197, 214, 301, 373, 382, 581 -> R.drawable.e24_k7_fast_crunchs
        45, 77, 221, 309, 333, 365, 374, 389, 405, 437, 446, 502, 582 -> R.drawable.e25_k8_seated_punchs
        5, 429, 583 -> R.drawable.e26_k9_side_reaches
        29, 109, 158, 261, 310, 334, 584 -> R.drawable.e27_k10_toe_reach
        182, 293, 390, 585 -> R.drawable.e28_k11_reverse_crunch
        101, 237, 246, 254, 586 -> R.drawable.e29_k12_full_crunch
        528 -> R.drawable.e3_b3_forward_and_back_bend
        93, 229, 587 -> R.drawable.e30_k13_single_side_up
        54, 325, 342, 349 -> R.drawable.e31_k14_to_v_sit
        173, 366, 397, 413, 478, 588 -> R.drawable.e32_k15_single_leg_raise
        22, 53, 165, 190, 430, 589 -> R.drawable.e33_k16_standing_throw_knee
        30, 86, 181, 421, 590 -> R.drawable.e34_k17_standing_single_leg_up
        6, 78, 85, 222, 294, 341, 591 -> R.drawable.e35_k18_t_position
        102, 269, 406, 477, 485, 494, 592 -> R.drawable.e36_k19_standing_to_push_up
        13, 142, 166, 358, 438, 454, 593 -> R.drawable.e37_k20_lying_leg_up
        69, 174, 253, 414, 453, 594 -> R.drawable.e38_k21_reverse_crunch
        277, 302, 398, 470, 501, 595 -> R.drawable.e39_k22_standing_shake
        136, 152, 529 -> R.drawable.e4_b4_forward_dive
        21, 189, 213, 245, 461 -> R.drawable.e40_k23_modified_plank
        70, 118, 262, 286, 381, 469 -> R.drawable.e41_k24_mountain_climb
        127, 335, 407, 554 -> R.drawable.e42_a1_seated_dips
        71, 143, 271, 287, 391, 555 -> R.drawable.e43_a2_triceps_push_up
        247, 423, 556, 574 -> R.drawable.e44_a3_bend_over_arm_extension
        87, 167, 351, 431, 557 -> R.drawable.e45_a4_pushup_overhead
        103, 111, 191, 295, 319, 471, 558, 571 -> R.drawable.e46_a5_lying_triceps_extensions
        39, 55, 119, 359, 487, 559 -> R.drawable.e47_a6_punch_left_right
        15, 95, 135, 383, 560 -> R.drawable.e48_a7_tricep_dips
        151, 231, 327, 495, 561, 572 -> R.drawable.e49_a8_knee_push_ups
        256, 530 -> R.drawable.e5_b5_back_and_forward_band
        7, 31, 183, 199, 223, 239, 255, 343, 463, 562, 573 -> R.drawable.e50_a9_modified_push_ups
        63, 175, 503, 563 -> R.drawable.e51_a10_slowed_pushups
        23, 303, 439, 564 -> R.drawable.e52_a11_lying_shoulder_extension
        367, 447, 565 -> R.drawable.e53_a12_push_up_strech
        47, 79, 263, 479, 566 -> R.drawable.e54_a13_front_overhead_raise
        159, 215, 415, 568, 569 -> R.drawable.e55_a14_front_raise
        399, 455, 567 -> R.drawable.e56_a15_air_curls
        207, 279, 311, 375, 570 -> R.drawable.e57_a16_single_arm_overhead_press
        163, 275, 291, 307, 356, 412, 460 -> R.drawable.e58_s1_bend_from_back
        28, 203, 243, 332 -> R.drawable.e59_s2_shoulder_mobility
        533 -> R.drawable.e6_b6_neck_rotation
        155, 219, 260, 283, 323, 339 -> R.drawable.e60_s3_camel_back_strech
        3, 116, 171, 180, 259, 276, 340, 355, 364, 459 -> R.drawable.e61_s4_floor_pull_up
        83, 140, 267, 404, 505 -> R.drawable.e62_s5_seated_side_reach_to_foot
        12, 20, 92, 483, 491, 506 -> R.drawable.e63_s6_seated_singe_side_reach_to_foot
        52, 172, 212, 315, 411 -> R.drawable.e64_s7_lying_knee_to_chest
        67, 91, 99, 131, 284, 396, 420 -> R.drawable.e65_s8_lying_knee_to_stomatch
        43, 115, 139, 147, 204, 395 -> R.drawable.e66_s9_lying_leg_to_sides
        4, 35, 84, 108, 195, 513 -> R.drawable.e67_s10_lying_leg_raise
        44, 124, 132, 235, 244, 299, 372, 379, 388, 403, 428, 492, 514 -> R.drawable.e68_s11_seated_knee_raise
        19, 59, 196, 347, 380, 443, 468, 499 -> R.drawable.e69_s12_strech_to_foots
        272, 534, 550 -> R.drawable.e7_b7_shoulder_rotation
        11, 75, 316, 363, 419, 427, 467, 484, 539 -> R.drawable.e70_s13_standing_leg_extension
        60, 100, 300, 387, 436, 451, 548 -> R.drawable.e71_s14_up_down_sumo
        27, 76, 251 -> R.drawable.e72_s15_curtsy_lunge_with_knee_raise_lift
        68, 156, 164, 188, 252, 331, 371, 435, 549 -> R.drawable.e73_s16_half_floor_pull_up
        36, 148, 211, 268, 292, 308, 348, 444, 476 -> R.drawable.e74_s17_down_to_up_clapping
        123, 179, 187, 324, 452, 519 -> R.drawable.e75_s18_shoulder_leg_streching
        228, 236, 507 -> R.drawable.e76_s19_standing_reach_to_foot
        51, 107, 220, 227, 475, 500, 520 -> R.drawable.e77_s20_jumping_sides
        24, 128, 200, 328, 508, 537 -> R.drawable.e78_y1_standing_shoulder_extension
        216, 440, 448, 456, 509 -> R.drawable.e79_y2_standing_leg_streching
        535 -> R.drawable.e8_b8_side_bends
        48, 96, 144, 184, 296, 510 -> R.drawable.e80_y3_curtsy_squat_fast
        280, 472, 511 -> R.drawable.e81_y4_seated_side_extensions
        192, 464, 512 -> R.drawable.e82_y5_single_leg_extension
        168, 320, 515 -> R.drawable.e83_y6_mountain_pushups
        104, 176, 208, 392, 424, 516 -> R.drawable.e84_y7_side_plank_leg_raise
        40, 304, 517 -> R.drawable.e85_y8_down_to_up_hand_raise
        32, 312, 336, 360, 518 -> R.drawable.e86_y9_plank_extension
        64, 524 -> R.drawable.e87_y10_plank_walk
        88, 240, 525 -> R.drawable.e88_y11_side_plank_extension
        352, 504, 523 -> R.drawable.e89_y12_plank_row
        248, 376, 400, 536 -> R.drawable.e9_b9_upper_body_rotation
        2, 41, 369, 596 -> R.drawable.e90_b1_bird_dog
        90, 178, 361, 597 -> R.drawable.e91_b2_jump_squat
        9, 49, 282, 377, 385, 598, 617 -> R.drawable.e92_b3_glute_bridge
        10, 34, 106, 121, 154, 170, 345, 394, 401, 521, 599 -> R.drawable.e93_b4_quadruped_hip_extension
        42, 105, 162, 242, 297, 434, 450, 600 -> R.drawable.e94_b5_plie_squat
        25, 138, 426, 458, 473, 552, 601 -> R.drawable.e95_b6_reverse_lunge_forward_hand
        1, 66, 82, 129, 145, 241, 249, 274, 346, 465, 474, 602, 618 -> R.drawable.e96_b7_fire_hydrants
        74, 81, 289, 313, 362, 409, 442, 490, 553, 619 -> R.drawable.e97_b8_clamshells
        58, 65, 417, 603, 622 -> R.drawable.e98_b9_split_squat
        17, 161, 290, 338, 402, 482, 604, 616 -> R.drawable.e99_b10_reverse_lunge
        else -> R.drawable.placehlder
    }
}
