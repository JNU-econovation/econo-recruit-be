const ApplicantSearch = () => {
  return (
    <form className="flex items-center py-2 px-4 bg-[#F9FBFF] rounded-2xl gap-2">
      <button>
        <img src="/icons/search-icon.svg" alt="search" />
      </button>
      <input
        className="p-2 color-[#B5B7C0] bg-transparent outline-none"
        type="text"
        placeholder="search"
      ></input>
    </form>
  );
};
export default ApplicantSearch;