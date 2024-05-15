// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Robot;
import frc.robot.base.modules.swerve.SwerveConstants;
import frc.robot.base.subsystems.swerve.SwerveAction;
import frc.robot.base.subsystems.swerve.SwerveDriveSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

public class AutoNoteTrackCommand extends Command {

  IntakeSubsystem m_intakeSubsystem;
  Robot m_robot;
  SwerveDriveSubsystem m_swerveDriveSubsystem;

  /** Creates a new AutoNoteTrackCommand. */
  public AutoNoteTrackCommand(Robot robot, IntakeSubsystem intakeSubsystem, SwerveDriveSubsystem swerveDriveSubsystem) {
    m_intakeSubsystem = intakeSubsystem;
    m_robot = robot;
    m_swerveDriveSubsystem = swerveDriveSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_robot.setFieldOriented(true);
    new SetSwerveActionCommand(m_robot, SwerveAction.AUTOAIMNOTE);

    double angularVelocity = 0;
    ChassisSpeeds chassisSpeeds = new ChassisSpeeds(0, 0, angularVelocity);
      SwerveModuleState[] moduleStates =
          SwerveConstants.kDriveKinematics.toSwerveModuleStates(chassisSpeeds);
      m_swerveDriveSubsystem.setModuleStates(moduleStates);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_robot.setFieldOriented(false);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_intakeSubsystem.hasNote();
  }
}
