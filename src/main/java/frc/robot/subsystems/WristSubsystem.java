package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkAbsoluteEncoder;
import com.revrobotics.SparkAbsoluteEncoder.Type;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycle;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.robot.Constants;
import frc.robot.base.RobotSystem;
import frc.robot.base.subsystems.SubsystemAction;
import frc.robot.base.subsystems.WCStaticSubsystem;
import java.util.List;
import org.littletonrobotics.junction.Logger;

// NEXT STEPS
// add encoder limits
// maybe set position function

public class WristSubsystem extends WCStaticSubsystem {

  // Buffer is value slightly above 0 to ensure doesn't smack
  private final double buffer = 0.0;

  private CANSparkMax m_wrist;
  private PIDController m_PidController;
  private double m_setPoint;
  private SparkAbsoluteEncoder m_absoluteEncoder;

  @Override
  protected double getBaseSpeed() {
    return 0.1;
  }

  @Override
  protected List<MotorController> initMotors() {
    m_wrist = new CANSparkMax(Constants.kWrist, MotorType.kBrushless);
    m_PidController = new PIDController(1.0, 0.0, 0.0);
    m_PidController.enableContinuousInput(0, 2 * Math.PI);
    m_absoluteEncoder = m_wrist.getAbsoluteEncoder(Type.kDutyCycle);
    // m_arm.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 1, 0);
    // TalonFXConfiguration config = new TalonFXConfiguration();
    return List.of(m_wrist);
  }

  // LOWER LIMIT ISNT ZERO IT WILL START IN AN IN BETWEEN
  @Override
  public void periodic() {
    // System.out.println("Wrist Position" + m_absoluteEncoder.getPosition());
    if (subsystemAction == SubsystemAction.UP && isAtUpperLimit()) {
      forwardMotors();
    } else if (subsystemAction == SubsystemAction.DOWN && isAtLowerLimit()) {
      reverseMotors();
    } else if (subsystemAction == SubsystemAction.POS) {
      double measurement =
          RobotSystem.isReal() ? m_absoluteEncoder.getPosition() * 2 * Math.PI : positionSim;
      double setpoint = m_setPoint * 2 * Math.PI;
      m_wrist.setVoltage(m_PidController.calculate(measurement, setpoint));
      if (measurement == setpoint) {
        subsystemAction = null;
      }
    } else {
      stopMotors();
    }
    positionSim += m_wrist.getAppliedOutput() * 0.02;
    Logger.recordOutput("wristOutput", m_wrist.getAppliedOutput());
    Logger.recordOutput(
        "wristPosition", RobotSystem.isReal() ? m_absoluteEncoder.getPosition() : positionSim);
    Logger.recordOutput("wristUP", subsystemAction == SubsystemAction.UP);
    Logger.recordOutput("wristDown", subsystemAction == SubsystemAction.DOWN);
    Logger.recordOutput("wristPos", subsystemAction == SubsystemAction.POS);
  }

  private boolean isAtUpperLimit() {
    double wristPosition = RobotSystem.isReal() ? m_absoluteEncoder.getPosition() : positionSim;
    return wristPosition < Constants.kWristEncoderMax + buffer;
  }

  private boolean isAtLowerLimit() {
    double wristPosition = RobotSystem.isReal() ? m_absoluteEncoder.getPosition() : positionSim;
    return wristPosition > Constants.kWristEncoderMin - buffer;
  }

  // public void forward() {
  //   subsystemAction = SubsystemAction.FORWARD;
  // }

  // public void reverse() {
  //   subsystemAction = SubsystemAction.REVERSE;
  // }

  public void up() {
    subsystemAction = SubsystemAction.UP;
  }

  public void down() {
    subsystemAction = SubsystemAction.DOWN;
  }

  public void pos(double setpoint) {
    m_setPoint = setpoint;
    subsystemAction = SubsystemAction.POS;
  }
}
