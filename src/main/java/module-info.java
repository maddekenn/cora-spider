module se.uu.ub.cora.spider {
	requires transitive se.uu.ub.cora.beefeater;
	requires transitive se.uu.ub.cora.bookkeeper;

	exports se.uu.ub.cora.spider.authentication;
	exports se.uu.ub.cora.spider.authorization;
	exports se.uu.ub.cora.spider.consistency;
	exports se.uu.ub.cora.spider.data;
	exports se.uu.ub.cora.spider.dependency;
	exports se.uu.ub.cora.spider.extended;
	exports se.uu.ub.cora.spider.record;
	exports se.uu.ub.cora.spider.record.storage;
	exports se.uu.ub.cora.spider.role;
	exports se.uu.ub.cora.spider.search;
	exports se.uu.ub.cora.spider.stream.storage;
}