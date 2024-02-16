package frc.robot.subsystems;

import com.ctre.phoenix6.configs.*;
import com.ctre.phoenix6.hardware.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

// NEXT STEPS
// add encoder limits
// maybe set position function

public class ArmSubsystem extends SubsystemBase {

  enum ArmAction {
    ON,
    OFF;
  }

  // Buffer is value slightly above 0 to ensure doesn't smack
  private double buffer = 0.0;

  private ArmAction rightBumperAction = ArmAction.OFF;
  private ArmAction leftBumperAction = ArmAction.OFF;

  private TalonFX m_arm;
  private double m_speed = 0.1;

  /** Creates a new Shooter. */
  public ArmSubsystem() {
    m_arm = new TalonFX(Constants.kArm);
    // m_arm.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 1, 0);
    // TalonFXConfiguration config = new TalonFXConfiguration();
  }

  @Override
  public void periodic() {
    System.out.println("Arm Position" + m_arm.getPosition().getValueAsDouble());
    if (rightBumperAction == ArmAction.ON
        && m_arm.getPosition().getValueAsDouble() > Constants.kArmEncoderMin + buffer) {
      m_arm.setVoltage(m_speed * 12.0);
    } else if (leftBumperAction == ArmAction.ON
        && m_arm.getPosition().getValueAsDouble() < Constants.kArmEncoderMax - buffer) {
      m_arm.setVoltage(m_speed * -12.0);
    } else {
      m_arm.setVoltage(0.0);
    }
  }

  public void rightBumperButton(boolean isPressed) {
    if (isPressed) {
      rightBumperAction = ArmAction.ON;
    } else {
      rightBumperAction = ArmAction.OFF;
    }
  }

  public void leftBumperButton(boolean isPressed) {
    if (isPressed) {
      leftBumperAction = ArmAction.ON;
    } else {
      leftBumperAction = ArmAction.OFF;
    }
  }
}