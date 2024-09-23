/**
 * 다음 기수 서류 합격자 관련 자동화 시 반영할 코드입니다. package
 * com.econovation.recruit.api.email.handler; @Component @RequiredArgsConstructor @Slf4j public
 * class ApplicantRegisterEventConfirmEmailHandler { private final CommonsEmailSender emailSender;
 * private final CardLoadPort cardLoadPort; private final AnswerAdaptor
 * answerAdaptor; @Value("${econovation.year}") private Integer year;
 *
 * <p>private final ApplicantQueryUseCase applicantQueryUseCase;
 *
 * <p>private final ExecutorService executor =
 * Executors.newCachedThreadPool(); @Async @TransactionalEventListener( classes =
 * ApplicantRegisterEvent.class, phase = TransactionPhase.AFTER_COMMIT) @Transactional(propagation =
 * Propagation.REQUIRES_NEW) public void handle(EmailSendEvent event) throws IOException {
 *
 * <p>applicantQueryUseCase.getApplicantsByYear(year); EmailTemplateType emailTemplateType =
 * event.getEmailTemplateType();
 *
 * <p>// Resource Loader setting classPath // String path = new File(".").getCanonicalPath(); // 현재
 * 작업 디렉토리를 가져옴 // String dir = path + "/documentPassed.csv"; // CSVReader csvReader = new
 * CSVReaderBuilder(new FileReader(dir)).withSkipLines(1).build(); try { csvReader.forEach( line ->
 * CompletableFuture.runAsync( () -> { String template = event.getMessage(); switch
 * (emailTemplateType) { case DOCUMENT_PASS: template = generatePassedTemplates(line, template);
 * break; case DOCUMENT_FAIL: template = generateFailedTemplate(line, template); break; case
 * INTERVIEW_PASS: template = generateInterviewPassedTemplate(line, template); break; case
 * INTERVIEW_FAIL: template = generateInterviewFailureTemplate(line, template); break; default:
 * log.error("이메일 발송 완료 : {}", emailTemplateType.name()); } emailSender.send( line[3], "에코노베이션 신입 모집
 * 서류전형 결과 안내", template); }, executor)); } catch (Exception e) { // Transactional Outbox Pattern을
 * 사용하면서 발생하는 예외를 잡아서 처리 log.error("서류 합격자 이메일 발송 실패"); } }
 */
/**
 * 서류 합격자 이메일 템플릿
 *
 * @param line ( 번호, 이름, 합격여부, 이메일, 면접 날짜, 면접 시간, 오픈채팅방 링크, 오픈채팅방 입장 마감일)
 *//*

   private String generatePassedTemplates(String[] line, String template) {
       return template.replace("%YEAR%", year.toString())
           .replace("%NAME%", line[1]) // 이름
           .replace("%DATE%", line[4]) // 면접 날짜
           .replace("%TIME%", line[5]) // 면접 시간
           .replace("%LINK%", line[6]) // 오픈채팅방 링크
           .replace("%KAKAOTALK_ENTRANCE_ENDDATE%", line[4]); // 오픈채팅방 입장 마감일
   }
   */
/**
 * 탈락 지원자 이메일 템플릿
 *
 * @param line ( 번호,이름,합격 상태,메일 주소)
 *//*

   private String generateFailedTemplate(String[] line, String template) {
       return template.replace("%NAME%", line[1]);
   }

   */
/**
 * 면접 합격자 이메일 템플릿
 *
 * @param line ( 번호, 이름, 합격여부, 이메일 오픈채팅방 입장 마감일), file ( 에코노베이션 포트폴리오 )
 *//*

   private String generateInterviewPassedTemplate(String[] line, String template) {
       return template.replace("%YEAR%", year.toString()).replace("%NAME%", line[1]); // 이름
   }

   */
/**
 * 면접 탈락자 이메일 템플릿
 *
 * @param line ( 번호, 이름, 합격여부, 이메일)
 *//*

       private String generateInterviewFailureTemplate(String[] line, String template) {
           return template.replace("%NAME%", line[1]);
       }
   }
   */
