import { interviewPopupBooleanState } from "@/src/stores/interview/Interview.atom";
import { useAtom } from "jotai";
import InterviewUserComponent from "./User.component";
import InterviewLabelComponent from "./Label.component";
import InterviewCommentComponent from "./Comment.component";

import InterviewDetailLeftComponent from "./DetailLeft.component";
import Board from "../common/board/Board.component";
import InterviewDetailRightComponent from "./DetailRight.component";

const InterviewBoardComponent = () => {
  const boardInterviewData = Array.from({ length: 10 }).map((_, i) => ({
    id: i,
    title: "[개발자]임채승",
    subElements: ["APP", "WEB", "4.5"],
    time: new Date(),
  }));

  return (
    <Board
      baseUrl={""}
      wapperClassname="divide-x"
      boardData={boardInterviewData}
    >
      <div className="flex flex-1 min-h-0">
        <div className="flex-1 overflow-auto px-12">
          <InterviewDetailLeftComponent />
        </div>
      </div>
      <div className="flex flex-1 min-h-0">
        <div className="flex-1 overflow-auto px-12">
          <InterviewDetailRightComponent />
        </div>
      </div>
    </Board>
  );
};

export default InterviewBoardComponent;