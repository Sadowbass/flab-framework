# 자제체작 프레임워크

---

## 안내사항

1. di시 파라미터 명으로 찾아서 주입하는 기능이 있기에 컴파일러 옵션에서 `-parameters`를 반드시 넣어주어야 작동합니다. (gradle, maven, javac 공통)  
   해당 옵션 없이 파라미터명을 리플렉션으로 찾는 방법을 도저히 찾지 못 하였습니다.
2. 스프링의 코드를 보고 만든 것이 아닌 순수하게 내 생각만으로 만든 것이라 내부 구현이 100% 다릅니다. (에러메시지나 용어는 많이 빌려서 사용)
3. `@Autowired` 어노테이션이 존재하지만 어느 생성자를 사용할지 선택용이고 필드 Injection 미구현입니다. (필드에 선언 자체가 불가능)

---
## 동작 구조
1. `SummerApplication.run()`으로 초기화를 시작합니다.
2. 현재는 하드코딩 되어있는 `Configurer` 클래스를 사용중이나 
후에 List 타입으로 여러종류의 `Configurer`의 추가 및 설정도 고민중입니다.  
아마 그때는 스프링을 매우 많이 참고해서 작업해야 할 듯 합니다. 
지금은 순수하게 제 능력으로 빈을 생성하고 처리할 방법을 찾기위한 학습이었습니다.
3. `Configurer.configure()`를 통해 설정합니다. 
`BeanAutoConfigurer`에서는 빈을 찾고 등록하는 일을 하며 `ComponentBeanFinder가` 구현되어 있습니다.  
추후에 `BeanAnnotationBeanFinder`를 추가해서 `@Bean` 어노테이션을 통해 빈을 추가하는 클래스를 추가할 수 있습니다. 
(파라미터 이름으로 빈을 주입하는 기능은 이것을 고려하여 만들었습니다)
4. 찾은 빈 목록을 `BeanFactory` 인터페이스의 구현체를 이용해 생성합니다.  
기본적으로 `DefaultBeanFactory` 클래스를 이용중 입니다.
5. 빈의 기본타입뿐 아니라 상위 클래스, 인터페이스를 이용하여 찾을 때도 주입이 되어야 해서 
타겟 목록 List뿐 아니라 클래스별 Map으로도 추가하고 만들어야 할 총 목록을 구합니다. (TestComponent -> ParentComponent -> Object 모두가 생성되어야 합니다)
6. 실제 빈의 생성은 `BeanCreationContext` 클래스를 이용하여 생성합니다.  
빈 생성중 의존관계에 있는 빈은 생성되어 있을 경우 가져다 사용하며 없으면 생성하며 집어 넣습니다. 이때 순환참조가 발생하면 예외를 던집니다. (에러메시지는 스프링처럼 친절하지 못합니다)
7. `BeanCreationContext`가 모든 빈을 생성하면 BeanFactory가 Context의 결과를 받아와서 생성된 Bean들의 목록에 집어넣습니다.  이때 중복된 빈 이름이 있으면 예외가 발생합니다.
8. 모든 처리가 끝난 후 생성 타겟 List와 Map을 비웁니다. 생성 결과만 별도의 클래스로 관리하면 BeanFactory가 메모리에서 지워질때 같이 지워지겠지만 여기서는 BeanFactory가 빈을 계속해서 제공하기 위해 참조를 유지함으로 메모리에서 지워지지 않습니다.  
9. 설정이 완료된 후 `SummerApplication`이 자신을 리턴하여 사용할 수 있습니다.