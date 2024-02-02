package rupp.com.kh.dto;


import lombok.*;
        import rupp.com.kh.enums.Gender;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private Gender gender;
    private String imgUrl;
    private String position;
}
