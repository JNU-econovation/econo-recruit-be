"use client";
import SortListComponent from "../common/SortList.component";
import { useSearchParams } from "next/navigation";

const ApplicantSortList = () => {
  const searchParmas = useSearchParams();
  const orderMenu = [
    { type: "newest", string: "최신순" },
    { type: "name", string: "이름순" },
  ];
  const order = searchParmas.get("order") ?? "newest";

  return <SortListComponent sortList={orderMenu} selected={order} />;
};

export default ApplicantSortList;