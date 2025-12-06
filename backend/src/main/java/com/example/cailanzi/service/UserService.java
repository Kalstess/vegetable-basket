package com.example.cailanzi.service;

import com.example.cailanzi.entity.Company;
import com.example.cailanzi.entity.User;
import com.example.cailanzi.entity.Vehicle;
import com.example.cailanzi.repository.CompanyRepository;
import com.example.cailanzi.repository.UserRepository;
import com.example.cailanzi.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final VehicleRepository vehicleRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByCompanyId(Long companyId) {
        return userRepository.findByCompanyId(companyId);
    }

    public List<User> findByVehicleId(Long vehicleId) {
        return userRepository.findByVehicleId(vehicleId);
    }

    public User create(User user) {
        log.info("Creating user: username={}, role={}", user.getUsername(), user.getRole());
        
        // 验证用户名唯一性
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 验证角色值
        if (user.getRole() == null) {
            log.warn("User role is null, setting default to COMPANY");
            user.setRole(User.UserRole.COMPANY);
        } else {
            log.debug("User role: {}", user.getRole().name());
        }

        // 编码密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(encodePassword(user.getPassword()));
        }

        // 验证企业关联（如果是企业用户）
        if (user.getRole() == User.UserRole.COMPANY) {
            if (user.getCompany() != null && user.getCompany().getId() != null) {
                Company company = companyRepository.findById(user.getCompany().getId())
                        .orElseThrow(() -> new IllegalArgumentException("企业不存在"));
                user.setCompany(company);
            } else {
                throw new IllegalArgumentException("企业用户必须关联企业");
            }
        }

        // 验证车辆关联（如果是司机用户）
        if (user.getRole() == User.UserRole.DRIVER) {
            if (user.getVehicle() != null && user.getVehicle().getId() != null) {
                Vehicle vehicle = vehicleRepository.findById(user.getVehicle().getId())
                        .orElseThrow(() -> new IllegalArgumentException("车辆不存在"));
                user.setVehicle(vehicle);
                // 司机用户也需要关联企业（通过车辆获取）
                if (vehicle.getCompany() != null) {
                    user.setCompany(vehicle.getCompany());
                }
            } else {
                throw new IllegalArgumentException("司机用户必须关联车辆");
            }
        }

        return userRepository.save(user);
    }

    public User update(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 更新基本信息
        if (userDetails.getNickname() != null) {
            user.setNickname(userDetails.getNickname());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }
        if (userDetails.getIsActive() != null) {
            user.setIsActive(userDetails.getIsActive());
        }

        // 更新密码（如果提供）
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(encodePassword(userDetails.getPassword()));
        }

        // 更新角色（仅管理员可以修改）
        if (userDetails.getRole() != null) {
            user.setRole(userDetails.getRole());
        }

        // 更新角色时，需要重新验证关联
        if (userDetails.getRole() != null && !userDetails.getRole().equals(user.getRole())) {
            // 角色变更，需要重新设置关联
            if (userDetails.getRole() == User.UserRole.COMPANY) {
                // 变为企业用户，必须关联企业
                if (userDetails.getCompany() == null || userDetails.getCompany().getId() == null) {
                    throw new IllegalArgumentException("企业用户必须关联企业");
                }
            } else if (userDetails.getRole() == User.UserRole.DRIVER) {
                // 变为司机用户，必须关联车辆
                if (userDetails.getVehicle() == null || userDetails.getVehicle().getId() == null) {
                    throw new IllegalArgumentException("司机用户必须关联车辆");
                }
            }
        }

        // 更新企业关联
        if (userDetails.getCompany() != null && userDetails.getCompany().getId() != null) {
            Company company = companyRepository.findById(userDetails.getCompany().getId())
                    .orElseThrow(() -> new IllegalArgumentException("企业不存在"));
            user.setCompany(company);
        } else if (userDetails.getCompany() == null) {
            // 如果角色是企业用户，不能清除企业关联
            if (user.getRole() == User.UserRole.COMPANY) {
                // 保持原有企业关联
            } else {
                // 其他角色可以清除企业关联
                user.setCompany(null);
            }
        }

        // 更新车辆关联
        if (userDetails.getVehicle() != null && userDetails.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(userDetails.getVehicle().getId())
                    .orElseThrow(() -> new IllegalArgumentException("车辆不存在"));
            user.setVehicle(vehicle);
            // 如果更新了车辆，同时更新企业关联（通过车辆获取）
            if (vehicle.getCompany() != null) {
                user.setCompany(vehicle.getCompany());
            }
        } else if (userDetails.getVehicle() == null) {
            // 如果角色是司机用户，不能清除车辆关联
            if (user.getRole() == User.UserRole.DRIVER) {
                // 保持原有车辆关联
            } else {
                // 其他角色可以清除车辆关联
                user.setVehicle(null);
            }
        }

        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("用户不存在");
        }
        userRepository.deleteById(id);
    }

    // 用户自己更新个人信息
    public User updateProfile(String username, User userDetails) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (userDetails.getNickname() != null) {
            user.setNickname(userDetails.getNickname());
        }
        if (userDetails.getEmail() != null) {
            user.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            user.setPhone(userDetails.getPhone());
        }

        // 更新密码（如果提供）
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(encodePassword(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    // 密码编码（使用SHA-256）
    private String encodePassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
}

