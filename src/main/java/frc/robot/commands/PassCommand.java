// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.base.io.Beambreak;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.WristSubsystem;

public class PassCommand extends Command {

  WristSubsystem m_wristSubsystem;
  FeederSubsystem m_feederSubsystem;
  ArmSubsystem m_armSubsystem;
  ShooterSubsystem m_shooterSubsystem;
  Beambreak m_beambreak;
  long dwellStart = 0L;

  /** Creates a new PassCommand. */
  public PassCommand(
      WristSubsystem wristSubsystem,
      FeederSubsystem feederSubsystem,
      ArmSubsystem armSubsystem,
      ShooterSubsystem shooterSubsystem,
      Beambreak beambreak) {
    m_armSubsystem = armSubsystem;
    m_feederSubsystem = feederSubsystem;
    m_wristSubsystem = wristSubsystem;
    m_shooterSubsystem = shooterSubsystem;
    m_beambreak = beambreak;

    addRequirements(m_armSubsystem, m_wristSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if (m_beambreak.isBroken()) {
      m_wristSubsystem.pos(Constants.kWristEncoderMin);

      m_shooterSubsystem.spinFastClose();
    }

    double wristPos = m_wristSubsystem.getWristPosition();
    double buffer = 0.02;
    if (m_shooterSubsystem.isVelocityTerminal()
        && wristPos >= Constants.kWristEncoderMin - buffer
        && wristPos <= Constants.kWristEncoderMin + buffer) {
      if (dwellStart == 0) {
        dwellStart = System.currentTimeMillis();
      } else if (System.currentTimeMillis() - dwellStart >= 250) {
        m_feederSubsystem.shoot();
      }
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_wristSubsystem.pos(Constants.kWristStow);
    m_feederSubsystem.stop();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_beambreak.isOpen();
  }
}
