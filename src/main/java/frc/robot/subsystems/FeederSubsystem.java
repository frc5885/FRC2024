package frc.robot.subsystems;

import com.revrobotics.CANSparkLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.robot.Constants;
import frc.robot.WCLogger;
import frc.robot.base.subsystems.SubsystemAction;
import frc.robot.base.subsystems.WCStaticSubsystem;
import java.util.List;

public class FeederSubsystem extends WCStaticSubsystem {

  CANSparkMax m_feeder;

  private boolean m_photonDied = false;

  @Override
  protected double getBaseSpeed() {
    return 0.5;
  }

  @Override
  protected List<MotorController> initMotors() {
    m_feeder = new CANSparkMax(Constants.kFeeder, MotorType.kBrushless);
    return List.of(m_feeder);
  }

  @Override
  public void periodic() {
    super.periodic();
    if (subsystemAction == SubsystemAction.INTAKE) {
      forwardMotors();
    } else if (subsystemAction == SubsystemAction.SHOOT) {
      m_feeder.setVoltage(12.0);
    } else if (subsystemAction == SubsystemAction.OUTTAKE) {
      reverseMotors();
    } else if (subsystemAction == SubsystemAction.EJECT) {
      m_feeder.setVoltage(-12.0);
    } else {
      stopMotors();
    }
  }

  @Override
  protected void putDebugDataPeriodic(boolean isRealRobot) {
    WCLogger.putNumber(this, "Voltage", m_feeder.getAppliedOutput());
    WCLogger.putNumber(this, "Current", m_feeder.getOutputCurrent());
    WCLogger.putAction(this, "Action", subsystemAction);
  }

  public void intake() {
    subsystemAction = SubsystemAction.INTAKE;
  }

  public void shoot() {
    subsystemAction = SubsystemAction.SHOOT;
  }

  public void outtake() {
    subsystemAction = SubsystemAction.OUTTAKE;
  }

  public void eject() {
    subsystemAction = SubsystemAction.EJECT;
  }

  public double getVelocity() {
    return m_feeder.getEncoder().getVelocity();
  }

  public boolean getPhotonDied() {
    return m_photonDied;
  }

  public void setPhotonDied(boolean condition) {
    m_photonDied = condition;
  }
}
